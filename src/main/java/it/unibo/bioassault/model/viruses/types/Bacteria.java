package it.unibo.bioassault.model.viruses.types;

import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.viruses.Virus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Bacteria extends Virus {

    private static BufferedImage sprite;

    static {
        try {
            sprite = ImageIO.read(SpikyVirus.class.getResource("/sprite/virus/bacteria-sprite.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Impossibile caricare /sprite/virus/bacteria-sprite.png");
            sprite = null;
        }
    }

    public Bacteria(int x, int y, ID id, Handler handler) {
        super(x, y, id, handler, 100, 2.0f);
    }

    @Override
    public void tick() {
        // 1. Usa la logica del padre per inseguire il player
        trackPlayer();

        // 2. Usa la logica del padre per muoversi e rimbalzare sui bordi
        updateMovementAndBounds();

    }

    @Override
    public void render(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, (int) x, (int) y, 60, 60, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillOval((int) x, (int) y, 32, 32);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }
}