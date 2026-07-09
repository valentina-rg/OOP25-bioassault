package it.unibo.bioassault.view;

import it.unibo.bioassault.model.Game;
import javax.swing.*;
import java.awt.*;

public class Window {

    public Window(int width, int height, String title, Game game){

        JFrame frame = new JFrame(title);

        // Finestra "di servizio": deve esistere solo perche' Game (l'engine)
        // ha bisogno di un JFrame reale per il proprio BufferStrategy, ma non
        // deve mai comparire come finestra vera per l'utente (niente Dock,
        // niente Cmd+Tab, niente titolo visibile). Tutto va impostato PRIMA
        // che la finestra diventi "displayable", cioe' prima di pack()/setVisible().
        //
        // NOTA (fix macOS): la sola posizione fuori schermo (-10000,-10000) non
        // basta su macOS, che a volte riporta la finestra dentro lo schermo
        // reale se la considera "showing". Per essere sicuri che non si veda
        // MAI, la rendiamo anche completamente trasparente (opacity 0) e senza
        // bordi/titolo (undecorated) oltre che fuori schermo.
        frame.setType(java.awt.Window.Type.UTILITY);
        frame.setUndecorated(true);

        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));

        frame.add(game);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();
        Insets insets = frame.getInsets();
        frame.setSize(width + insets.left + insets.right,
                height + insets.top + insets.bottom);

        // Completamente invisibile, indipendentemente da dove finisce posizionata.
        try {
            frame.setOpacity(0f);
        } catch (final UnsupportedOperationException | IllegalComponentStateException e) {
            // Se la piattaforma non supporta l'opacita' su finestre undecorated,
            // ci affidiamo comunque alla posizione fuori schermo qui sotto.
        }

        // Finestra fuori schermo: l'arena visibile e' quella di GameWindow/ArenaPanel
        frame.setLocation(-10000, -10000);
        frame.setFocusableWindowState(false);
        frame.setVisible(true);
    }
}
