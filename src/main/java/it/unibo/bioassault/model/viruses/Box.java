package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.GameObject;

import java.awt.*;

public class Box extends GameObject {

    public Box(int x, int y) {

        super(x, y);

        velX = 1;
    }


    public void tick() {
        x += velX;
        y += velY;
    }


    public void render(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(x, y, 32, 32);
    }


    public Rectangle getBounds() {
        return null;
    }
}
