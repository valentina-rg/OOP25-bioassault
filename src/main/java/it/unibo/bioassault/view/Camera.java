package it.unibo.bioassault.view;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;

public class Camera {

    private float x, y;
    private static final float CAMERA_SPEED = 0.08f;

    public Camera() {
        this.x = 0;
        this.y = 0;
    }

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void tick(GameObject object) {

        final float halfWidth = Game.WINDOW_WIDTH / 2f;
        final float halfHeight = Game.WINDOW_HEIGHT / 2f;

        // posizione del player al centro della finestra
        float targetX = object.getX() - halfWidth;
        float targetY = object.getY() - halfHeight;

        // inseguimento del player
        x += (targetX - x) * CAMERA_SPEED;
        y += (targetY - y) * CAMERA_SPEED;

        // clamp ai bordi della mappa
        x = limit(x, 0, Game.WORLD_WIDTH  - Game.WINDOW_WIDTH);
        y = limit(y, 0, Game.WORLD_HEIGHT - Game.WINDOW_HEIGHT);
    }
    

    //getter:
    public float getX(){
        return x;
    }
    public float getY() {
        return y;
    }

    //setter:
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }

    private float limit(final float value, final float min, final float max) {

    if (value < min) return min;

    if (value > max) return max;

    return value;

}

}
