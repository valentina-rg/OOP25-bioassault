package it.unibo.bioassault.model.combat;

import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.viruses.Virus;

import java.awt.*;
import java.util.LinkedList;

public class Projectile extends GameObject {

    private final Handler handler; // Handler degli oggetti di gioco
    private final int damage; // Danno del proiettile
    private final int size = 8; // Dimensione del proiettile

    public Projectile(final int x, final int y, final Handler handler, final float velX, final float velY, final int damage) {
        super(x, y, ID.Projectile);
        this.handler = handler;
        this.velX = velX;
        this.velY = velY;
        this.damage = damage;
    }

    @Override
    public void tick() {
        this.x += this.velX; // Sposta il proiettile sull'asse X
        this.y += this.velY; // Sposta il proiettile sull'asse Y

        for (final GameObject obj : new LinkedList<>(handler.object)) { // Copia della lista per evitare problemi durante la rimozione
            if (obj instanceof Virus virus) { // Controlla solo i virus
                if (this.getBounds().intersects(virus.getBounds())) { // Collisione proiettile-virus
                    virus.takeDamage(this.damage); // Il virus subisce danno
                    handler.removeObject(this); // Il proiettile sparisce dopo il colpo
                    return;
                }
            }
        }
    }

    @Override
    public void render(final Graphics g) {
        g.setColor(Color.GREEN);
        g.fillOval((int) this.x, (int) this.y, this.size, this.size);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) this.x, (int) this.y, this.size, this.size);
    }
}