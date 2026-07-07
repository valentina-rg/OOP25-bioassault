package it.unibo.bioassault.view;

import it.unibo.bioassault.model.Game;

import javax.swing.*;
import java.awt.*;

public class Window {

    public Window(int width, int height, String title, Game game){

        JFrame frame = new JFrame(title);

        //dimensioni
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));

        frame.add(game);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        Insets insets = frame.getInsets();
        frame.setSize(width + insets.left + insets.right,
                height + insets.top + insets.bottom);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
