package it.unibo.bioassault.model.combat;

import it.unibo.bioassault.model.combat.collisions.CollisionSystem;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.Game;
import java.awt.*;

public class Projectile extends GameObject {

    private final Handler handler; 
    private final int damage;
    private final String weaponName; 
    private final int size = 13; 
    
    public Projectile(final int x, final int y, final Handler handler, final float velX, final float velY, final int damage,final String weaponName, ID id) {
        super(x, y, id);
        this.handler = handler;
        this.velX = velX;
        this.velY = velY;
        this.damage = damage;
        this.weaponName = weaponName;
    }
    /**
    * Restituisce il nome dell'arma che ha generato il proiettile.
    *
     * @return nome dell'arma
    */
    public String getWeaponName() {
        return this.weaponName;
    }
    /**
    * Aggiorna la posizione del proiettile e controlla le collisioni.
    * Il proiettile viene rimosso quando colpisce un virus
    * oppure quando esce dai limiti del mondo di gioco.
     */

    @Override
    public void tick() {
        this.x += this.velX; 
        this.y += this.velY; 

        if (isOutsideWorld()) {
            handler.removeObject(this);
            return;
        }

       CollisionSystem.handleProjectileVirusCollisions(handler,this,this.damage);
    }

    /**
    * Verifica se il proiettile si trova fuori dal mondo di gioco.
    *
    * @return true se il proiettile è fuori dai limiti
    */

    private boolean isOutsideWorld() {
        return this.x < 0
            || this.y < 0
            || this.x > Game.WORLD_WIDTH
            || this.y > Game.WORLD_HEIGHT;
    }

    /**
     * Disegna il proiettile.
     *
     * @param g contesto grafico
     */

    @Override
    public void render(final Graphics g) {
        g.setColor(Color.GREEN);
        g.fillOval((int) this.x, (int) this.y, this.size, this.size);
    }

    /**
     * Restituisce la hitbox del proiettile.
     *
     * @return area occupata dal proiettile
     */

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) this.x, (int) this.y, this.size, this.size);
    }
}