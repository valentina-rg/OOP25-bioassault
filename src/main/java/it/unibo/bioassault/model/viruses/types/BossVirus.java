package it.unibo.bioassault.model.viruses.types;

import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.viruses.Virus;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Boss finale: virus unico, molto grande e con molti HP.
 */
public class BossVirus extends Virus {

    private static final int BASE_HP = 500;
    private static final float BASE_SPEED = 1.2f;
    private static final int VISUAL_SIZE = 300; // ~5x rispetto a SpikyVirus (60)
    private static final int BOUNDS_SIZE = 220; // hitbox proporzionalmente grande

    private static BufferedImage sprite;

    static {
        try {
            sprite = ImageIO.read(BossVirus.class.getResource("/sprite/virus/boss_virus_sprite_transparent.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Impossibile caricare /sprite/virus/boss_virus_sprite_transparent.png");
            sprite = null;
        }
    }

    public BossVirus(int x, int y, ID id, Handler handler) {
        super(x, y, id, handler, BASE_HP, BASE_SPEED);
    }

    @Override
    public void tick() {
        trackPlayer();
        updateMovementAndBounds();
    }

    @Override
    public void render(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, (int) x, (int) y, VISUAL_SIZE, VISUAL_SIZE, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillOval((int) x, (int) y, VISUAL_SIZE, VISUAL_SIZE);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, BOUNDS_SIZE, BOUNDS_SIZE);
    }
}