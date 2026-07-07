package it.unibo.bioassault.controller;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.view.GameScreens;

import javax.swing.JFrame;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller del flusso di gioco.
 * Vincolo di progetto: Game.java (l'engine) NON viene toccato.
 * Questa classe quindi non tocca Game: lo crea, si aggancia ai suoi
 * metodi pubblici (addKeyListener, ereditato da Canvas) e governa
 * dall'esterno menu, pausa e game over.
 * La pausa usa la tecnica del "parcheggio": gli oggetti vengono
 * temporaneamente rimossi dall'Handler (il tick di Game gira a vuoto
 * su una lista vuota) e reinseriti alla ripresa.
 */
public class GameController {

    /** Stati del flusso di gioco. */
    private enum State { MENU, PLAYING, PAUSED }

    private State state = State.MENU;

    private Game game;               // l'engine (immutato) di gioco
    private Handler handler;         // riferimento all'handler di Game
    private JFrame menuFrame;        // finestra del menu iniziale

    /** Parcheggio degli oggetti durante la pausa. */
    private final List<GameObject> parked = new ArrayList<>();

    public GameController() {
        showMainMenu();
    }

    // ---- Menu iniziale -------------------------------------------------

    private void showMainMenu() {
        state = State.MENU;

        menuFrame = new JFrame("BioAssault");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setResizable(false);

        final GameScreens.MainMenuScreen menu = new GameScreens.MainMenuScreen(
                this::startGame,          // INIZIA
                () -> System.exit(0));    // ESCI
        menu.setPreferredSize(new Dimension(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT));

        menuFrame.add(menu);
        menuFrame.pack();
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }

    // ---- Avvio partita --------------------------------------------------

    private void startGame() {
        // Chiudo il menu (e fermo i suoi timer di animazione).
        if (menuFrame != null) {
            for (final Component c : menuFrame.getContentPane().getComponents()) {
                if (c instanceof GameScreens.DisposableOverlay d) {
                    d.dispose();
                }
            }
            menuFrame.dispose();
            menuFrame = null;
        }

        // Accendo l'engine: crea la sua Window e parte il suo thread.
        game = new Game();
        // TODO: handler = game.getHandler();  <-- in attesa della decisione
        //       del gruppo (getter in Game oppure reflection).

        // Aggancio gli input dall'esterno, tramite l'API pubblica di Canvas.
        game.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
            }
        });

        state = State.PLAYING;
    }

    // ---- Pausa col parcheggio -------------------------------------------

    private void togglePause() {
        if (handler == null) {
            // Accesso all'handler non ancora disponibile: segnalo e basta.
            System.out.println("[Pausa richiesta - in attesa dell'accesso all'Handler]");
            return;
        }
        if (state == State.PLAYING) {
            // PAUSA: parcheggio tutti gli oggetti; il tick di Game gira a vuoto.
            parked.clear();
            parked.addAll(handler.object);
            handler.object.clear();
            state = State.PAUSED;
        } else if (state == State.PAUSED) {
            // RIPRESA: rimetto tutto in campo.
            handler.object.addAll(parked);
            parked.clear();
            state = State.PLAYING;
        }
    }
}