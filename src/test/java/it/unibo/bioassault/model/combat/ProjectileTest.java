package it.unibo.bioassault.model.combat;

import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.viruses.Virus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test automatici per la classe Projectile.
 */
class ProjectileTest {

    /**
     * Virus fittizio usato solo per i test, dato che Virus è astratta.
     */
    private static class TestVirus extends Virus {
        TestVirus(final int x, final int y, final Handler handler, final int hp) {
            super(x, y, ID.Enemy, handler, hp, 1.0f);
        }

        @Override
        public void tick() {
        }

        @Override
        public void render(final java.awt.Graphics g) {
        }

        @Override
        public java.awt.Rectangle getBounds() {
            return new java.awt.Rectangle((int) getX(), (int) getY(), 32, 32);
        }
    }

    @Test
    void projectileShouldMoveAccordingToVelocity() {
        final Handler handler = new Handler();
        final Projectile projectile = new Projectile(10, 20, handler, 2f, 3f, 5, ID.Projectile);

        projectile.tick();

        assertEquals(12f, projectile.getX(), 0.0001f);
        assertEquals(23f, projectile.getY(), 0.0001f);
    }

    @Test
    void projectileShouldDamageVirusAndRemoveItselfOnCollision() {
        final Handler handler = new Handler();

        final Virus virus = new TestVirus(50, 50, handler, 20);
        final Projectile projectile = new Projectile(50, 50, handler, 0f, 0f, 10, ID.Projectile);

        handler.addObject(virus);
        handler.addObject(projectile);

        final int initialHp = virus.getHp();

        projectile.tick();

        assertEquals(initialHp - 10, virus.getHp());
        assertFalse(handler.object.contains(projectile));
    }

    @Test
    void projectileShouldNotBeRemovedWhenNoCollisionOccurs() {
        final Handler handler = new Handler();

        final Virus virus = new TestVirus(500, 500, handler, 20);
        final Projectile projectile = new Projectile(10, 10, handler, 0f, 0f, 10, ID.Projectile);

        handler.addObject(virus);
        handler.addObject(projectile);

        final int initialHp = virus.getHp();

        projectile.tick();

        assertEquals(initialHp, virus.getHp());
        assertTrue(handler.object.contains(projectile));
    }

    @Test
    void projectileShouldKillVirusWhenDamageExceedsHp() {
        final Handler handler = new Handler();

        final Virus virus = new TestVirus(50, 50, handler, 5);
        final Projectile projectile = new Projectile(50, 50, handler, 0f, 0f, 10, ID.Projectile);

        handler.addObject(virus);
        handler.addObject(projectile);

        projectile.tick();

        assertTrue(virus.isDead());
        assertEquals(0, virus.getHp());
    }
}