package it.unibo.bioassault.model.player;


import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;

import java.awt.*;

//ATTENZIONE: player di prova per verificare lo spawn dei nemici. codice base da migliorare e da espandere
public class Player extends GameObject {

    Handler handler;
    private int hp = 100; // Punti vita iniziali della cellula

    public Player(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
    }

    public boolean hasStartedMoving = false;



    @Override
    public void tick() {
        // input -> velocità

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

    // Applica un danno alla cellula
    public void takeDamage(final int damage) {

        if (damage < 0) { // Il danno non può essere negativo
         throw new IllegalArgumentException(
               "Il danno non può essere negativo"
         );
        }

     this.hp = Math.max(
            0,                  // HP minimi consentiti
            this.hp - damage    // HP dopo il danno
        );
    }

    // Restituisce gli HP correnti della cellula
    public int getHp() {
        return this.hp; // Restituisce gli HP attuali
    }

    // Verifica se la cellula è morta
    public boolean isDead() {
      return this.hp <= 0; // Morta se gli HP sono pari a zero
    }
}
