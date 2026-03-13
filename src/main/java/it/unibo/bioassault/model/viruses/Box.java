package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.ID;

import java.awt.*;

public class Box extends GameObject {

    public Box(int x, int y, ID id) {

        super(x, y, id);

        velX = 1;
    }


    public void tick() {
        x += velX;
        y += velY;
    }


    public void render(Graphics g) {
        g.setColor(Color.red);
        g.fillRect((int) x, (int) y, 32, 32);
    }


    public Rectangle getBounds() {
        return null;
    }
}
