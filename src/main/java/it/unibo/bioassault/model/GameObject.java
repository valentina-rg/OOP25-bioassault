package it.unibo.bioassault.model;

//classe in comune per tutti gli oggetti del gioco

import java.awt.*;

public abstract class GameObject {

    protected int x, y; //posizione dell'oggetto
    protected float velX = 0, velY = 0; //velocità oggetto

    public GameObject(int x, int y){
        this.x = x;
        this.y = y;
    }

    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getBounds();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }
}

