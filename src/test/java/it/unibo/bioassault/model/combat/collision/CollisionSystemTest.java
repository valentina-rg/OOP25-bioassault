package it.unibo.bioassault.model.combat.collisions;

import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.ID;
import org.junit.jupiter.api.Test;

import java.awt.Graphics;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test relativi al riconoscimento delle collisioni tra hitbox.
 */
class CollisionSystemTest {

    /**
     * Oggetto fittizio utilizzato esclusivamente per creare
     * hitbox con posizione e dimensioni controllate.
     */
    private static class DummyGameObject extends GameObject {

        DummyGameObject(final int x, final int y) {
            super(x, y, ID.Enemy);
        }

        @Override
        public void tick() {
            // Nessun aggiornamento necessario per questo test.
        }

        @Override
        public void render(final Graphics g) {
            // Nessun rendering necessario per questo test.
        }

        @Override
        public Rectangle getBounds() {
            return new Rectangle(
                    (int) getX(),
                    (int) getY(),
                    10,
                    10
            );
        }
    }

    /**
     * Verifica che due hitbox sovrapposte siano considerate
     * in collisione e che due hitbox separate non lo siano.
     */
    @Test
    void collidesShouldRecognizeOverlappingAndSeparatedBounds() {
        final GameObject first =
                new DummyGameObject(0, 0);

        final GameObject overlapping =
                new DummyGameObject(5, 5);

        final GameObject separated =
                new DummyGameObject(20, 20);

        assertTrue(
                CollisionSystem.collides(first, overlapping)
        );

        assertFalse(
                CollisionSystem.collides(first, separated)
        );
    }
}