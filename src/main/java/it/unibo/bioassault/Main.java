package it.unibo.bioassault;

import it.unibo.bioassault.controller.GameController;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(final String[] args) {
        // GameController crea la finestra e fa partire il game loop.
        SwingUtilities.invokeLater(GameController::new);
    }
}