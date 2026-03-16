package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import java.util.Random;

public abstract class Virus extends GameObject {

    protected Handler handler;
    protected Random r = new Random();

    // Attributi che ogni tipo di virus avrà
    protected int hp;
    protected float speed;
    protected int choose = 0;

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
}