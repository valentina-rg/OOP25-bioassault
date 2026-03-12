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
        if (handler.isUp()) {
            velY = -5;
        } else if (!handler.isDown()) {
            velY = 0;
        }

        if (handler.isDown()) {
            velY = 5;
        } else if (!handler.isUp()) {
            velY = 0;
        }

        // X
        if (handler.isRight()) {
            velX = 5;
        } else if (!handler.isLeft()) {
            velX = 0;
        }

        if (handler.isLeft()) {
            velX = -5;
        } else if (!handler.isRight()) {
            velX = 0;
        }

    }


    public void render(Graphics g) {
        g.setColor(Color.green);
        g.fillRect(x, y, 32, 48);
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 48);
    }
}
