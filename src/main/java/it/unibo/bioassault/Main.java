package it.unibo.bioassault;

import it.unibo.bioassault.model.Game;

public class Main {
    public static void main(String[] args) {
        //System.out.println("Il progetto BioAssault è partito!");

        Game game = new Game();

        // Dobbiamo creare un Thread per eseguire il Game Loop
        Thread gameThread = new Thread(game);
        gameThread.start();
    }
}