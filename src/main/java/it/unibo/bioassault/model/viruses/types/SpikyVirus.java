package it.unibo.bioassault.model.viruses.types;

import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.viruses.Virus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpikyVirus extends Virus {

    private static BufferedImage sprite;
    private double spikeAngleOffset = 0;

    static {
        try {
            sprite = ImageIO.read(SpikyVirus.class.getResource("/sprite/virus/sprite-virus.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Impossibile caricare /sprite/virus/sprite-virus.png");
            sprite = null;
        }
    }

    public SpikyVirus(int x, int y, ID id, Handler handler) {
        super(x, y, id, handler, 100, 2.0f);
    }

    @Override
    public void tick() {
        trackPlayer();
        updateMovementAndBounds();
        //fai ruotare le punte
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

        g2d.setColor(new Color(100, 0, 255)); // Colore viola per gli spike
        g2d.setStroke(new BasicStroke(3));

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