package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.player.Player;
import  main.java.it.unibo.bioassault.model.stats.RunStats;

import java.util.Random;

public abstract class Virus extends GameObject {

    protected Handler handler;
    protected Random r = new Random();
    protected float mx; // Player Position throu observer
    protected float my; // Player Position throu observer
    protected Player player;

    // Attributi che ogni tipo di virus avrà
    protected int hp;
    protected float speed;
    protected int choose = 0;

    private boolean isBig;

    public Virus(int x, int y, ID id, Handler handler, int hp, float speed) {
        super(x, y, id);
        this.handler = handler;
        this.hp = hp;
        this.speed = speed;

        // Velocità iniziale random comune a tutti i virus
        velX = r.nextInt(7) - 3;
        velY = r.nextInt(7) - 3;
        if (velX == 0 && velY == 0) velX = 1;
    }

    /**
     * Metodo comune per inseguire il player.
     * Le classi figlie lo chiameranno nel loro tick().
     */
    protected void trackPlayer() {
        GameObject player = null;
        for (GameObject obj : handler.object) {
            if (obj.getId() == ID.Player) {
                player = obj;
                break;
            }
        }

        if (player == null) return;

        it.unibo.bioassault.model.player.Player p =
                (it.unibo.bioassault.model.player.Player) player;

        if (!p.hasStartedMoving) {
            velX = 0; velY = 0;
        } else {
            float dx = p.getX() - x;
            float dy = p.getY() - y;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length != 0) {
                dx /= length;
                dy /= length;
            }
            velX = dx * this.speed;
            velY = dy * this.speed;
        }
    }

    /**
     * Metodo comune per gestire i rimbalzi a schermo e il movimento erratico.
     */
    protected void updateMovementAndBounds() {
        x += velX;
        y += velY;

        choose = r.nextInt(10);
        if (choose == 0) {
            velX = r.nextInt(7) - 3;
            velY = r.nextInt(7) - 3;
            if (velX == 0 && velY == 0) velX = 1;
        }

        if (x <= 0 || x >= Game.WORLD_WIDTH - 32) velX *= -1;
        if (y <= 0 || y >= Game.WORLD_HEIGHT - 32) velY *= -1;
    }

    public final void update() {
        mx = this.player.getX();
        my = this.player.getY();
    }

    public void reachTarget() {
        this.setX(this.getX() + this.velX);
        this.setY(this.getY() + this.velY);

        final float angle = (float) Math.atan2(my - this.getY() + 8, mx - this.getX() + 4);

        this.velX = (float) ((this.speed) * Math.cos(angle));
        this.velY = (float) ((this.speed) * Math.sin(angle));
    }

    protected GameObject getPlayer() {
        if (handler == null || handler.object == null) {
            return null;
        }

        for (GameObject obj : handler.object) {
            if (obj.getId() == ID.Player) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Imposta la posizione iniziale del virus in un raggio casuale attorno al giocatore.
     */
    public final void setStartingPosition(final float minDistance, final float maxDistance) {
        GameObject player = getPlayer();
        float px = (player != null) ? player.getX() : 0;
        float py = (player != null) ? player.getY() : 0;

        // Calcola un angolo casuale e una distanza casuale tra i due limiti
        float angle = (float) (Math.random() * 2 * Math.PI);
        float distance = minDistance + r.nextFloat() * (maxDistance - minDistance);

        // Imposta le coordinate del virus in base a calcoli trigonometrici dal player
        this.x = px + (float) (distance * Math.cos(angle));
        this.y = py + (float) (distance * Math.sin(angle));
    }

    /**
     * Define if a virus is a big one.
     */
    public void setIsBig(boolean isBig) {
        if (isBig) {
            this.hp *= 2;
        }
        this.isBig = isBig;
    }

    public boolean isBig() {
        return isBig;
    }

    public kills(){
        if (VirusCombatUtils.isDead(this)) {
            stats.recordKill(this.getClass().getSimpleName(), this.isBig(), false);
            handler.removeObject(this);
        }
    }
}
