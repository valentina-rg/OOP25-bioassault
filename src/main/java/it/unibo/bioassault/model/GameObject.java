package it.unibo.bioassault.model;

//classe in comune per tutti gli oggetti del gioco

import java.awt.*;

public abstract class GameObject implements IGameObject {

    protected float x, y; //posizione dell'oggetto
    protected float velX = 0, velY = 0; //velocità oggetto
    protected ID id;


    public GameObject(int x, int y, ID id){
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getBounds();


    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
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

