package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Virus extends GameObject {

    private Handler handler;
    private static BufferedImage sprite;
    Random r = new Random();
    int choose = 0;
    int hp = 100;

    // Nuova variabile per gestire l'animazione di rotazione degli spike
    private double spikeAngleOffset = 0;

    static {
        try {
            sprite = ImageIO.read(
                    Virus.class.getResource("/sprite/virus/sprite-virus.png")
            );
        } catch (IOException | IllegalArgumentException e) {
            // log di errore chiaro
            System.err.println("Impossibile caricare /sprite/virus/sprite-virus.png");
            e.printStackTrace();
            sprite = null; // fallback
        }
    }

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


        spikeAngleOffset += 0.05;
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;


        int centerX = (int) x + 30;
        int centerY = (int) y + 30;

        int numSpikes = 12;
        int innerRadius = 20;
        int outerRadius = 25;

        g2d.setColor(new Color(100, 0, 255)); // Colore rosso scuro per gli spike
        g2d.setStroke(new BasicStroke(3));  // Spessore della linea: 3 pixel

        for (int i = 0; i < numSpikes; i++) {
            double angle = (2 * Math.PI * i / numSpikes) + spikeAngleOffset;

            int x1 = (int) (centerX + Math.cos(angle) * innerRadius);
            int y1 = (int) (centerY + Math.sin(angle) * innerRadius);
            int x2 = (int) (centerX + Math.cos(angle) * outerRadius);
            int y2 = (int) (centerY + Math.sin(angle) * outerRadius);

            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.setStroke(new BasicStroke(1));

        if (sprite != null) {
            g.drawImage(sprite, (int) x, (int) y, 60, 60, null);
        } else {
            g.setColor(Color.yellow);
            g.fillRect((int) x, (int) y, 32, 32);
        }

    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }
}