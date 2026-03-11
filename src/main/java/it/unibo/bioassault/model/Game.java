package it.unibo.bioassault.model;

import it.unibo.bioassault.view.Window;

import java.awt.*;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

    public Game(){
        new Window(1000, 563, "BioAssault", this);
    }

    public void run() {

    }


}
