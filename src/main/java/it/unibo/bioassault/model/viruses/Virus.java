package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;

import java.awt.*;
import java.util.Random;

public class Virus extends GameObject {

    private Handler handler;
    Random r = new Random();
    int choose = 0;
    int hp = 100;

    public Virus(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;

        // velocità iniziale random
        velX = r.nextInt(7) - 3; // -3..3
        velY = r.nextInt(7) - 3;
        if (velX == 0 && velY == 0) velX = 1;
    }

    @Override
    public void tick() {

        GameObject player = null;
        for (GameObject obj : handler.object) {
            if (obj.getId() == ID.Player) {
                player = obj;
                break;
            }
        }

        if (player == null) {
            return;
        }

        it.unibo.bioassault.model.player.Player p =
                (it.unibo.bioassault.model.player.Player) player;

        // se il player non si è mai mosso, il virus resta fermo
        if (!p.hasStartedMoving) {
            velX = 0; velY = 0;  // facoltativo
        } else {
            // da qui in poi insegue SEMPRE il player, anche se lui si ferma
            float dx = p.getX() - x;
            float dy = p.getY() - y;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length != 0) {
                dx /= length;
                dy /= length;
            }
            float speed = 2.0f; // velocità di inseguimento
            velX = dx * speed;
            velY = dy * speed;
        }

        // aggiorna posizione
        x += velX;
        y += velY;

        choose = r.nextInt(10);
        if (choose == 0) {
            velX = r.nextInt(7) - 3;
            velY = r.nextInt(7) - 3;
            if (velX == 0 && velY == 0) velX = 1;
        }

        // rimbalzo sui bordi del mondo (opzionale)
        if (x <= 0 || x >= Game.WORLD_WIDTH - 32) {
            velX *= -1;
        }
        if (y <= 0 || y >= Game.WORLD_HEIGHT - 32) {
            velY *= -1;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.yellow);
        g.fillRect((int) x, (int) y, 32, 32);

        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.green);
        g2d.draw(getBoundsBig());
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    public Rectangle getBoundsBig() {
        return new Rectangle((int) x - 16, (int) y - 16, 64, 64);
    }
}
