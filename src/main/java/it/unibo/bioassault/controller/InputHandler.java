package it.unibo.bioassault.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.EnumSet;
import java.util.Set;

/**
 * Gestore avanzato dell'input da tastiera.
 *
 * Rispetto a KeyInput (che aggiorna i flag booleani nell'Handler),
 * questa classe usa un EnumSet di direzioni per tracciare i tasti premuti.
 * Il vantaggio e' che EnumSet e' piu' efficiente e type-safe rispetto
 * a una lista di stringhe o a booleani separati.
 *
 * Gestisce anche azioni "one-shot" (pausa, conferma, scelta upgrade)
 * con il pattern "consuma": una volta letto il valore, il flag viene resettato
 * cosi' la stessa pressione non viene letta piu' volte nel game loop.
 *
 * Viene registrato come KeyListener su GameWindow.
 */
public class InputHandler extends KeyAdapter {

    /**
     * Enum per le direzioni di movimento.
     * Uso un enum invece di stringhe per evitare typo e
     * per sfruttare EnumSet che e' molto piu' veloce di HashSet.
     */
    public enum Direction { UP, DOWN, LEFT, RIGHT }

    // Insieme delle direzioni attualmente premute (piu' tasti contemporaneamente ok)
    private final Set<Direction> activeDirections = EnumSet.noneOf(Direction.class);

    // Flag per le azioni one-shot
    private boolean pausePressed   = false;
    private boolean confirmPressed = false;
    private int     upgradeChoice  = 0;   // 0 = nessuna, 1/2/3 = upgrade scelto

    // ---- Metodi di KeyAdapter che override-o ----

    @Override
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            // Movimento (WASD + frecce direzionali)
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> activeDirections.add(Direction.UP);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> activeDirections.add(Direction.DOWN);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> activeDirections.add(Direction.LEFT);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> activeDirections.add(Direction.RIGHT);
            // Azioni generali
            case KeyEvent.VK_ESCAPE               -> pausePressed   = true;
            case KeyEvent.VK_ENTER                -> confirmPressed = true;
            // Selezione upgrade al level-up (tasti 1, 2, 3)
            case KeyEvent.VK_1                    -> upgradeChoice  = 1;
            case KeyEvent.VK_2                    -> upgradeChoice  = 2;
            case KeyEvent.VK_3                    -> upgradeChoice  = 3;
            default -> { }
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> activeDirections.remove(Direction.UP);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> activeDirections.remove(Direction.DOWN);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> activeDirections.remove(Direction.LEFT);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> activeDirections.remove(Direction.RIGHT);
            case KeyEvent.VK_ESCAPE               -> pausePressed   = false;
            case KeyEvent.VK_ENTER                -> confirmPressed = false;
            default -> { }
        }
    }

    // ---- API pubblica: il GameController interroga questi metodi ogni frame ----

    /**
     * Restituisce il vettore di movimento in base ai tasti attivi.
     * Es: W + D premuti insieme -> {1, -1} (diagonale in alto a destra).
     *
     * @return array [dx, dy] con valori -1, 0 o 1
     */
    public int[] getMovementVector() {
        int dx = 0, dy = 0;
        if (activeDirections.contains(Direction.LEFT))  dx -= 1;
        if (activeDirections.contains(Direction.RIGHT)) dx += 1;
        if (activeDirections.contains(Direction.UP))    dy -= 1;
        if (activeDirections.contains(Direction.DOWN))  dy += 1;
        return new int[]{dx, dy};
    }

    /** Torna true se almeno una direzione e' attiva (il player si sta muovendo). */
    public boolean isMoving() {
        return !activeDirections.isEmpty();
    }

    /**
     * Legge e resetta il flag di pausa.
     * Il reset immediato evita che una pressione di ESC venga letta
     * piu' volte da frame successivi del game loop.
     */
    public boolean consumePause() {
        final boolean v = pausePressed;
        pausePressed = false;
        return v;
    }

    /** Come consumePause() ma per il tasto ENTER (conferma nelle schermate). */
    public boolean consumeConfirm() {
        final boolean v = confirmPressed;
        confirmPressed = false;
        return v;
    }

    /**
     * Legge e resetta la scelta dell'upgrade (1, 2 o 3).
     * @return 1-3 se un tasto numerico e' stato premuto, 0 altrimenti
     */
    public int consumeUpgradeChoice() {
        final int v = upgradeChoice;
        upgradeChoice = 0;
        return v;
    }
}
