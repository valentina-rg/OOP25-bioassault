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
        frame.setDefaultCloseOperation(JFrame. EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); //avvio del gioco la finestra è al centro
        frame.setVisible(true);

    }

}
