package it.unibo.bioassault.controller;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.player.Player;
import it.unibo.bioassault.view.GameScreens;
import it.unibo.bioassault.view.GameWindow;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private enum State { MENU, PLAYING, PAUSED, GAME_OVER }

    private static final int FRAME_MS = 16;

    private State state = State.MENU;

    private Game game;
    private Handler handler;
    private Player player;
    private JFrame menuFrame;
    private GameWindow window;        // finestra vera: arena disegnata da ArenaPanel + HUD

    private final List<GameObject> parked = new ArrayList<>();
    private final SnapshotBuilder snapshotBuilder = new SnapshotBuilder();
    private Timer hudLoop;

    private long startTimeMs;
    private int lastSurvivalSeconds;

    public GameController() {
        showMainMenu();
    }

    private void showMainMenu() {
        state = State.MENU;
        if (hudLoop != null) {
            hudLoop.stop();
        }
        stopCurrentGame();
        if (window != null) {
            window.dispose();
            window = null;
        }

        menuFrame = new JFrame("BioAssault");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setResizable(false);

        final GameScreens.MainMenuScreen menu = new GameScreens.MainMenuScreen(
                this::startGame,
                () -> System.exit(0));
        menu.setPreferredSize(new Dimension(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT));

        menuFrame.add(menu);
        menuFrame.pack();
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }

    private void startGame() {
        if (menuFrame != null) {
            for (final Component c : menuFrame.getContentPane().getComponents()) {
                if (c instanceof GameScreens.DisposableOverlay d) {
                    d.dispose();
                }
            }
            menuFrame.dispose();
            menuFrame = null;
        }

        stopCurrentGame();

        game = new Game();
        handler = HandlerReader.extractHandler(game);
        player = findPlayer();

        window = new GameWindow();
        window.addKeyListener(new KeyInput(handler));
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
            }
        });
        window.setVisible(true);
        window.requestFocusInWindow();

        startTimeMs = System.currentTimeMillis();
        state = State.PLAYING;

        hudLoop = new Timer(FRAME_MS, e -> {
            if (state != State.PLAYING) {
                return;
            }
            checkGameOver();
            if (state == State.PLAYING) {
                window.updateGameState(snapshotBuilder.build(handler, startTimeMs));
            }
        });
        hudLoop.start();
    }

    /**
     * Ferma il motore precedente (thread + finestra invisibile) prima di
     * sostituirlo con uno nuovo, cosi' non resta acceso in background.
     */
    private void stopCurrentGame() {
        if (game != null) {
            game.shutdown();
            game = null;
        }
    }

    private Player findPlayer() {
        for (final GameObject obj : handler.object) {
            if (obj instanceof Player p) {
                return p;
            }
        }
        return null;
    }

    private void togglePause() {
        if (handler == null) {
            return;
        }
        if (state == State.PLAYING) {
            parked.clear();
            parked.addAll(handler.object);
            handler.object.clear();
            state = State.PAUSED;
            window.showOverlay(new GameScreens.PauseScreen(
                    this::resumeGame,
                    this::showMainMenu));

        } else if (state == State.PAUSED) {
            resumeGame();
        }
    }

    private void resumeGame() {
        handler.object.addAll(parked);
        parked.clear();
        window.removeCurrentOverlay();
        window.requestFocusInWindow();
        state = State.PLAYING;
    }

    private void checkGameOver() {
        if (player != null && player.isDead()) {
            lastSurvivalSeconds = (int) ((System.currentTimeMillis() - startTimeMs) / 1000);
            showGameOver();
        }
    }

    private void showGameOver() {
        state = State.GAME_OVER;
        hudLoop.stop();

        final GameScreens.GameOverScreen.Stats stats =
                new GameScreens.GameOverScreen.Stats(
                        lastSurvivalSeconds,
                        1, 1, 0, 0, false);

        window.showOverlay(new GameScreens.GameOverScreen(
                stats,
                this::startGame,
                this::showMainMenu));
    }
}