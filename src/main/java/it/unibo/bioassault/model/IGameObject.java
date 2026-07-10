package it.unibo.bioassault.model;

import it.unibo.bioassault.controller.ITickingObject;

import java.awt.Graphics;
import java.awt.Rectangle;

public interface IGameObject extends ITickingObject {
    void render(Graphics g);

    Rectangle getBounds();

    float getX();
    float getY();

    void setX(float x);
    void setY(float y);

    ID getId();
}