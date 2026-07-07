package it.unibo.bioassault.model.player;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.viruses.Virus;
import it.unibo.bioassault.model.combat.Projectile;
import it.unibo.bioassault.model.combat.weapons.AntibodyWeapon;
import it.unibo.bioassault.model.combat.weapons.Weapon;

import java.awt.*;

/**
 * Rappresenta la cellula controllata dal giocatore.
 * Gestisce movimento, collisioni, punti vita e utilizzo dell'arma.
 */  
public class Player extends GameObject {

    Handler handler;
    private int hp = 100; 
    private int shootCooldown = 0; 
    private final Weapon weapon = new AntibodyWeapon(); // Arma usata dal player
    private float lastDirX = 1;
    private float lastDirY = 0;
    public boolean hasStartedMoving = false;

    public Player(int x, int y, ID id, Handler handler) {
        super(x, y, id);      
        this.handler = handler;
    }

   

    @Override
    public void tick() {
    updateMovement();
    updateShootingDirection();
    handleCollisions();
    updateShooting();
    }
    /**
    * Aggiorna la velocità e la posizione del player in base all'input.
    * Mantiene inoltre il player all'interno dei limiti del mondo di gioco.
    */
    private void updateMovement() {
         if (handler.isUp() && !handler.isDown()) {
        velY = -5;
    } else if (handler.isDown() && !handler.isUp()) {
        velY = 5;
    } else {
        velY = 0;
    }

    if (handler.isRight() && !handler.isLeft()) {
        velX = 5;
    } else if (handler.isLeft() && !handler.isRight()) {
        velX = -5;
    } else {
        velX = 0;
    }

    x += velX;
    y += velY;

    x = Math.max(0, Math.min(x, Game.WORLD_WIDTH - 32));
    y = Math.max(0, Math.min(y, Game.WORLD_HEIGHT - 48));
    }

    /**
    * Aggiorna la direzione dello sparo quando il player si muove.
    * Per scelta di gameplay, il proiettile viene sparato nella direzione
    * opposta rispetto a quella del movimento.
    */
    private void updateShootingDirection() {
        if (velX == 0 && velY == 0) {
        return;
    }

    hasStartedMoving = true;

    if (velX > 0) {
        lastDirX = -1;
        lastDirY = 0;
    } else if (velX < 0) {
        lastDirX = 1;
        lastDirY = 0;
    } else if (velY > 0) {
        lastDirX = 0;
        lastDirY = -1;
    } else {
        lastDirX = 0;
        lastDirY = 1;
    }
    }
    /**
    * Controlla le collisioni tra il player e i virus.
    * Quando le rispettive hitbox si intersecano, il player subisce danno.
    */
    private void handleCollisions() {
        for (final GameObject object : handler.object) {
            if (object instanceof Virus
                && this.getBounds().intersects(object.getBounds())) {
                this.takeDamage(1);
            }
        }
    }

    private void updateShooting() {
        if (shootCooldown > 0) {
            shootCooldown--;
            return;
        }

    handler.addObject(new Projectile(
        (int) x + 32,
        (int) y + 20,
        handler,
        lastDirX * weapon.getProjectileSpeed(),
        lastDirY * weapon.getProjectileSpeed(),
        weapon.getDamage(),
        ID.Projectile
    ));

    shootCooldown = 30;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect((int) x, (int) y, 32, 48);

    }

    @Override
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
        return this.hp;
    }

    // Verifica se la cellula è morta
    public boolean isDead() {
      return this.hp <= 0; 
    }
}
