package it.unibo.bioassault.model.player;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.combat.Projectile;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.viruses.Virus;
import it.unibo.bioassault.model.combat.weapons.AntibodyWeapon;
import it.unibo.bioassault.model.combat.weapons.Weapon;

import java.awt.*;

//ATTENZIONE: player di prova per verificare lo spawn dei nemici. codice base da migliorare e da espandere
public class Player extends GameObject {

    Handler handler;
    private int hp = 100; // Punti vita iniziali della cellula
    private int shootCooldown = 0; // Tempo di attesa tra uno sparo e l'altro
    private final AntibodyWeapon weapon = new AntibodyWeapon(); // Arma usata dal player

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
        x = Math.max(0, Math.min(x, Game.WORLD_WIDTH - 32));   // 32 = larghezza player
        y = Math.max(0, Math.min(y, Game.WORLD_HEIGHT - 48));  // 48 = altezza player

        if (velX != 0 || velY != 0) {
            hasStartedMoving = true;

        }
        

        // Collisione player-virus
        for (GameObject obj : handler.object) { // Scorre tutti gli oggetti del gioco
            if (obj instanceof Virus) { // Controlla solo i virus
                if (this.getBounds().intersects(obj.getBounds())) { // Se le hitbox si sovrappongono
                this.takeDamage(1); // Il player perde 1 HP
                }
            }
        }

        if (this.shootCooldown > 0) { // Se il cooldown è attivo
            this.shootCooldown--; // Diminuisce il cooldown
        }

        if (this.shootCooldown == 0) { // Se il player può sparare
           handler.addObject(
        new Projectile(
                (int) this.x + 32,
                (int) this.y + 20,
                handler,
                this.weapon.getProjectileSpeed(),
                0,
                this.weapon.getDamage(),
                ID.Projectile
        )
);
            this.shootCooldown = 30; // Imposta il cooldown dello sparo
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
