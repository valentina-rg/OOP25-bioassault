package it.unibo.bioassault.model.player;


import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;

import java.awt.*;

//ATTENZIONE: player di prova per verificare lo spawn dei nemici. codice base da migliorare e da espandere
public class Player extends GameObject {

    private Handler handler;

    private static final float BASE_SPEED_REFERENCE = 150.0f;
    private static final float BASE_PIXEL_SPEED = 5.0f;


    public Player(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
    }

    public boolean hasStartedMoving = false;

    /** Converts PlayerStats.moveSpeed to actual pixel-per-tick velocity. */
    private float resolvedSpeed() {
        return (handler.getPlayerStats().getMoveSpeed() / BASE_SPEED_REFERENCE) * BASE_PIXEL_SPEED;
    }

    @Override
    public void tick() {
        // input -> velocità
        final float spd = resolvedSpeed();
        
        // ASSE Y
        if (handler.isUp() && !handler.isDown()) {
            velY = -5;
        } else if (handler.isDown() && !handler.isUp()) {
            velY = 5;
        } else {
            velY = 0;
        }

        // ASSE X
        if (handler.isRight() && !handler.isLeft()) {
            velX = 5;
        } else if (handler.isLeft() && !handler.isRight()) {
            velX = -5;
        } else {
            velX = 0;
        }

        x += velX;
        y += velY;

        if (velX != 0 || velY != 0) {
            hasStartedMoving = true;
        }
    }





    public void render(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect((int) x, (int) y, 32, 48);

    }


    public Rectangle getBounds() {
        return new Rectangle((int) x, (int)y, 32, 48);
    }
}
