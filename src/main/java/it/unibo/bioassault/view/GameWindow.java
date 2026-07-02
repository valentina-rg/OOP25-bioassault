package it.unibo.bioassault.view;

import it.unibo.bioassault.controller.InputHandler;
import it.unibo.bioassault.model.GameSnapshot;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale del gioco (JFrame).
 *
 * Usa un JLayeredPane per sovrapporre i pannelli:
 *   DEFAULT_LAYER → ArenaPanel  (rendering del mondo di gioco)
 *   PALETTE_LAYER → HudPanel    (overlay HP/XP/timer, sempre visibile)
 *   MODAL_LAYER   → schermata attiva (menu / pausa / level-up / game-over)
 *
 * Il GameController tiene un riferimento a questa finestra e la aggiorna
 * chiamando updateGameState() a ogni frame, e showOverlay() per le schermate.
 *
 * NOTA DI INTEGRAZIONE: questa classe sostituisce Window.java e il Canvas
 * di Game.java. Per integrarla con il ciclo di gioco dei compagni,
 * il render di Game deve chiamare window.updateGameState(snapshot) invece
 * di disegnare direttamente su Graphics.
 */
public class GameWindow extends JFrame {

    // Dimensioni finestra (devono corrispondere a Game.WINDOW_WIDTH/HEIGHT)
    public static final int WIDTH  = 1000;
    public static final int HEIGHT = 563;

    private final ArenaPanel   arenaPanel;
    private final HudPanel     hudPanel;
    private final JLayeredPane layers;
    private final InputHandler inputHandler;

    // Overlay attualmente visibile (pausa, level-up, ecc.)
    private JPanel currentOverlay = null;

    public GameWindow() {
        super("BioAssault");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setResizable(false);

        // InputHandler gestisce la tastiera
        inputHandler = new InputHandler();

        // JLayeredPane come contenitore principale
        layers = new JLayeredPane();
        layers.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // Arena: layer base (si vede sotto tutto)
        arenaPanel = new ArenaPanel();
        arenaPanel.setBounds(0, 0, WIDTH, HEIGHT);
        layers.add(arenaPanel, JLayeredPane.DEFAULT_LAYER);

        // HUD: sopra all'arena, sempre visibile durante il gioco
        hudPanel = new HudPanel();
        hudPanel.setBounds(0, 0, WIDTH, HEIGHT);
        layers.add(hudPanel, JLayeredPane.PALETTE_LAYER);

        setContentPane(layers);

        // Registro l'InputHandler sulla finestra per ricevere i tasti
        addKeyListener(inputHandler);
        setFocusable(true);

        pack();
        setLocationRelativeTo(null); // centra la finestra sullo schermo
    }

    // ------------------------------------------------------------------ //
    //  API pubblica per il GameController
    // ------------------------------------------------------------------ //

    /**
     * Aggiorna arena e HUD con il nuovo snapshot di gioco.
     * Deve essere chiamato da SwingUtilities.invokeLater() nel game loop
     * per rispettare il thread di Swing.
     */
    public void updateGameState(final GameSnapshot snap) {
        arenaPanel.updateSnapshot(snap);
        hudPanel.updateSnapshot(snap);
    }

    /**
     * Mostra una schermata overlay (es. menu di pausa, level-up).
     * Rimuove automaticamente l'overlay precedente se presente.
     */
    public void showOverlay(final JPanel overlay) {
        removeCurrentOverlay();
        currentOverlay = overlay;
        overlay.setBounds(0, 0, getContentPane().getWidth(), getContentPane().getHeight());
        layers.add(overlay, JLayeredPane.MODAL_LAYER);
        layers.revalidate();
        layers.repaint();
    }

    /** Rimuove l'overlay corrente (es. chiudendo la pausa). */
    public void removeCurrentOverlay() {
        if (currentOverlay != null) {
            layers.remove(currentOverlay);
            currentOverlay = null;
            layers.revalidate();
            layers.repaint();
        }
    }

    // ---- Getter per il GameController ---------------------------------

    public InputHandler getInputHandler() { return inputHandler;  }
    public ArenaPanel   getArenaPanel()   { return arenaPanel;    }
    public HudPanel     getHudPanel()     { return hudPanel;      }
}
