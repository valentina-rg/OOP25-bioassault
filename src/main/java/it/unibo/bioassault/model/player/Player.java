package it.unibo.bioassault.model.player;


import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;

import java.awt.*;

//ATTENZIONE: player di prova per verificare lo spawn dei nemici. codice base da migliorare e da espandere
public class Player extends GameObject {

    Handler handler;

    public Player(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
    }


    public void tick() {
        x += velX;
        y += velY;

        //movimento
        int dirY = 0;
        if (handler.isUp()) {
            dirY -= 1;
        }
        if (handler.isDown()) {
            dirY += 1;
        }
        velY = dirY * 5;

    }


    public void render(Graphics g) {

    }


    public Rectangle getBounds() {
        return null;
    }
}
