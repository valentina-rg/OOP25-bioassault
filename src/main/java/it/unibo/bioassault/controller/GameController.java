package it.unibo.bioassault.controller;

import it.unibo.bioassault.model.viruses.types.Bacteria;
import it.unibo.bioassault.model.EnemyData;
import it.unibo.bioassault.model.EnemyType;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.GameSnapshot;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.ProjectileData;
import it.unibo.bioassault.model.combat.Projectile;
import it.unibo.bioassault.model.player.Player;
import it.unibo.bioassault.model.viruses.Virus;
import it.unibo.bioassault.model.viruses.VirusSpawner;
import it.unibo.bioassault.model.viruses.types.SpikyVirus;
import it.unibo.bioassault.view.GameScreens;
import it.unibo.bioassault.view.GameWindow;

import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller del gioco (parte VIEW/CONTROLLER del progetto).
 * Gestisce anche il flusso delle schermate:
 *   MENU -> (INIZIA) -> PLAYING <-> (ESC) PAUSED
 *   PLAYING -> (player morto) -> GAME_OVER -> (RIGIOCA) -> PLAYING
 *                                          -> (MENU)    -> MENU
 */
public class GameController {

    /** Stati possibili del gioco. */
    private enum State { MENU, PLAYING, PAUSED, GAME_OVER }

    /** Durata di un frame in millisecondi (~60 fps). */
    private static final int FRAME_MS = 16;

    /** HP massimi del player (Player parte con 100 hp, non espone il massimo). */
    private static final int PLAYER_MAX_HP = 100;

    private final Handler    handler;
    private final GameWindow window;
    private final Timer      gameLoop;

    // Ricreati a ogni nuova partita
    private Player       player;
    private VirusSpawner spawner;

    private State state = State.MENU;

    /** Momento di avvio della partita, per il timer di sopravvivenza. */
    private long startTimeMs;

    /** Ultimo tempo di sopravvivenza, salvato per la schermata di game over. */
    private int lastSurvivalSeconds;

    public GameController() {
        handler = new Handler();

        window = new GameWindow();
        // Movimento WASD/frecce: KeyInput del model, che imposta i flag sull'Handler
        window.addKeyListener(new KeyInput(handler));
        // Tasti "di sistema": listener nostro, separato dal KeyInput del model
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                // ESC = pausa / riprendi
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
                // DEBUG: K = elimina il player per testare il game over
                // (le collisioni del model non fanno ancora danno).
                // TODO: rimuovere prima della consegna.
                if (e.getKeyCode() == KeyEvent.VK_K && state == State.PLAYING) {
                    player.takeDamage(1000);
                }
            }
        });
        window.setVisible(true);

        // Game loop: tick/render lavorano solo quando lo stato e' PLAYING
        gameLoop = new Timer(FRAME_MS, e -> {
            tick();
            render();
        });

        showMainMenu();
    }

    // ------------------------------------------------------------------ //
    //  Flusso delle schermate
    // ------------------------------------------------------------------ //

    /** Mostra il menu principale (stato iniziale, o ritorno dal game over). */
    private void showMainMenu() {
        state = State.MENU;
        gameLoop.stop();
        window.showOverlay(new GameScreens.MainMenuScreen(
                this::startNewGame,          // INIZIA
                () -> System.exit(0)         // ESCI
        ));
    }

    /** Prepara una nuova partita e la avvia. */
    private void startNewGame() {
        // Ripulisco gli oggetti della partita precedente (la lista object
        // e' pubblica nell'Handler del model)
        handler.object.clear();

        player = new Player(100, 100, ID.Player, handler);
        handler.addObject(player);
        spawner = new VirusSpawner(handler);

        startTimeMs = System.currentTimeMillis();

        window.removeCurrentOverlay();
        window.requestFocusInWindow(); // il focus torna alla finestra per i tasti
        state = State.PLAYING;
        gameLoop.start();
    }

    /** ESC: alterna pausa e ripresa (attivo solo durante la partita). */
    private void togglePause() {
        if (state == State.PLAYING) {
            state = State.PAUSED;
            gameLoop.stop();
            window.showOverlay(new GameScreens.PauseScreen(
                    this::resumeGame,        // RIPRENDI
                    this::showMainMenu       // MENU
            ));
        } else if (state == State.PAUSED) {
            resumeGame();
        }
    }

    /** Riprende la partita dalla pausa. */
    private void resumeGame() {
        window.removeCurrentOverlay();
        window.requestFocusInWindow();
        state = State.PLAYING;
        gameLoop.start();
    }

    /** Il player e' morto: ferma tutto e mostra le statistiche. */
    private void showGameOver() {
        state = State.GAME_OVER;
        gameLoop.stop();

        // Statistiche: per ora solo quelle che il model traccia davvero.
        // wave/level/kills/danno sono placeholder finche' il model non li implementa.
        final GameScreens.GameOverScreen.Stats stats =
                new GameScreens.GameOverScreen.Stats(
                        lastSurvivalSeconds, // tempo sopravvissuto
                        1,                   // ondata raggiunta (TODO model)
                        1,                   // livello finale   (TODO model)
                        0,                   // nemici eliminati (TODO model)
                        0,                   // danno inflitto   (TODO model)
                        false                // sconfitta
                );

        window.showOverlay(new GameScreens.GameOverScreen(
                stats,
                this::startNewGame,          // RIGIOCA
                this::showMainMenu           // MENU PRINCIPALE
        ));
    }

    // ------------------------------------------------------------------ //
    //  Game loop
    // ------------------------------------------------------------------ //

    /** Aggiorna la logica di gioco: stessi passi del tick() di Game. */
    private void tick() {
        if (state != State.PLAYING) {
            return;
        }

        handler.tick();
        if (spawner != null) {
            spawner.tick();
        }

        // Fine partita quando il player esaurisce gli HP.
        // Il check sta DOPO handler.tick(): i danni vengono applicati li'.
        if (player != null && player.isDead()) {
            lastSurvivalSeconds =
                    (int) ((System.currentTimeMillis() - startTimeMs) / 1000);
            showGameOver();
        }
    }

    /** Costruisce lo snapshot e lo consegna alla view. */
    private void render() {
        if (state != State.PLAYING) {
            return;
        }
        window.updateGameState(buildSnapshot());
    }

    // ------------------------------------------------------------------ //
    //  Costruzione dello snapshot per la view
    // ------------------------------------------------------------------ //

    private GameSnapshot buildSnapshot() {
        final GameSnapshot snap = new GameSnapshot();

        final List<EnemyData>      enemies     = new ArrayList<>();
        final List<ProjectileData> projectiles = new ArrayList<>();

        // Copia della lista: gli oggetti possono essere aggiunti/rimossi durante il tick
        for (final GameObject obj : new ArrayList<>(handler.object)) {

            if (obj instanceof Player p) {
                // Il model usa l'angolo in alto a sinistra di un rettangolo 32x48;
                // la view disegna la cellula centrata, quindi converto al centro.
                snap.playerX     = p.getX() + 16;
                snap.playerY     = p.getY() + 24;
                snap.playerHp    = p.getHp();
                snap.playerMaxHp = PLAYER_MAX_HP;

            } else if (obj instanceof Virus v) {
                // Mappo la classe del virus sul tipo grafico della view.
                // I tipi futuri (es. boss -> ELITE) si aggiungono qui.
                final EnemyType type =
                        (v instanceof SpikyVirus) ? EnemyType.BASIC
                      : (v instanceof Bacteria)   ? EnemyType.FAST
                      : EnemyType.FAST;

                // Il model usa l'angolo in alto a sinistra dei bounds 32x32;
                // la view disegna centrato: converto al centro della hitbox.
                // TODO: usare v.getMaxHp() quando il model lo esporra'.
                enemies.add(new EnemyData(v.getX() + 16, v.getY() + 16,
                                          v.getHp(), Math.max(1, v.getHp()), type));

            } else if (obj instanceof Projectile pr) {
                // Il model disegna il proiettile dall'angolo (size 8),
                // la view dal centro: sposto di meta' dimensione.
                projectiles.add(new ProjectileData(pr.getX() + 4, pr.getY() + 4,
                                                   false, 0f));
            }
        }

        snap.enemies         = enemies;
        snap.projectiles     = projectiles;
        snap.xpOrbs          = new ArrayList<>(); // non ancora nel model
        snap.enemiesOnScreen = enemies.size();

        // ---- Progressione: valori provvisori finche' il model non li implementa ----
        snap.level        = 1;
        snap.xp           = 0;
        snap.xpToNext     = 100;
        snap.wave         = 1;
        snap.isInvincible = false;

        snap.survivalSeconds =
                (int) ((System.currentTimeMillis() - startTimeMs) / 1000);

        // Slot armi: per ora solo l'anticorpo di base nel primo slot
        snap.weapons = new String[] {"Anticorpo", null, null, null, null, null};

        return snap;
    }
}