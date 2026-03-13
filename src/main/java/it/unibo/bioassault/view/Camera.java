package it.unibo.bioassault.view;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;

public class Camera {

    private float x, y;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void tick(GameObject object) {

        final float halfWidth = Game.WINDOW_WIDTH / 2f;
        final float halfHeight = Game.WINDOW_HEIGHT / 2f;

        // posizione target: player al centro della finestra
        float targetX = object.getX() - halfWidth;
        float targetY = object.getY() - halfHeight;

        // inseguimento morbido
        x += (targetX - x) * 0.1f;
        y += (targetY - y) * 0.1f;

        // clamp ai bordi del MONDO
        if (x < 0) x = 0;
        if (x > Game.WORLD_WIDTH - Game.WINDOW_WIDTH) {
            x = Game.WORLD_WIDTH - Game.WINDOW_WIDTH;
        }
        if (y < 0) y = 0;
        if (y > Game.WORLD_HEIGHT - Game.WINDOW_HEIGHT) {
            y = Game.WORLD_HEIGHT - Game.WINDOW_HEIGHT;
        }
    }



    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}
