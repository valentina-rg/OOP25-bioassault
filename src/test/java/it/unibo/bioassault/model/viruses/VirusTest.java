package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VirusTest {

    private static class TestVirus extends Virus {
        TestVirus(final int hp) {
            super(0, 0, ID.Enemy, new Handler(), hp, 1.0f);
        }

        @Override
        public void tick() {
            //
        }

        @Override
        public void render(java.awt.Graphics g) {
            //
        }

        @Override
        public java.awt.Rectangle getBounds() {
            return new java.awt.Rectangle(0, 0, 0, 0);
        }
    }

    @Test
    void takeDamageShouldReduceHp() {
        TestVirus virus = new TestVirus(10);

        virus.takeDamage(3);

        assertEquals(7, virus.getHp());
        assertFalse(virus.isDead());
    }

    @Test
    void takeDamageShouldNotGoBelowZero() {
        TestVirus virus = new TestVirus(5);

        virus.takeDamage(10);

        assertEquals(0, virus.getHp());
        assertTrue(virus.isDead());
    }

    @Test
    void negativeDamageShouldThrowException() {
        TestVirus virus = new TestVirus(10);

        assertThrows(IllegalArgumentException.class, () -> virus.takeDamage(-1));
    }

    @Test
    void setIsBigShouldDoubleHpWhenTrue() {
        TestVirus virus = new TestVirus(8);

        virus.setIsBig(true);

        assertEquals(16, virus.getHp());
    }
}