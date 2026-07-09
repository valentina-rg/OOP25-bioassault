package it.unibo.bioassault.model;

import java.awt.*;
import java.util.LinkedList;

public class Handler {

    public LinkedList<GameObject> object = new LinkedList<GameObject>();//array di oggetti


    private boolean up = false, down = false, right = false, left = false;


    public void tick(){
        //loop che cicla su tutti gli oggetti di gioco
        for(int i = 0; i < object.size(); i++){
            GameObject tempObject = object.get(i);

            tempObject.tick();
        }
    }

    public void render(Graphics g){
        for(int i = 0; i < object.size(); i++){
            GameObject tempObject = object.get(i);

            tempObject.render(g);
        }
    }

    public void addObject(GameObject tempObject){
        object.add(tempObject);
    }

    public void removeObject(GameObject tempObject){
        object.remove(tempObject);
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    private boolean switchWeapon;

    /**
     * Verifica se è stato premuto il comando per cambiare arma.
     *
     * @return true se il comando di cambio arma è attivo
     */
    public boolean isSwitchWeapon() {
        return this.switchWeapon;
    }

    /**
     * Imposta lo stato del comando per cambiare arma.
     *
     * @param switchWeapon nuovo stato del comando
     */
    public void setSwitchWeapon(final boolean switchWeapon) {
        this.switchWeapon = switchWeapon;
    }
}
