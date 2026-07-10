package it.unibo.bioassault.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ViewDataTest {

    @Test
    void enemyDataShouldStoreConstructorValues() {
        final EnemyData enemy = new EnemyData(10.5f, 20.5f, 40, 100, EnemyType.BASIC);

        assertEquals(10.5f, enemy.x, 0.0001f);
        assertEquals(20.5f, enemy.y, 0.0001f);
        assertEquals(40, enemy.hp);
        assertEquals(100, enemy.maxHp);
        assertEquals(EnemyType.BASIC, enemy.type);
    }

    @Test
    void enemyDataAnglesShouldStartAtZero() {
        final EnemyData enemy = new EnemyData(0, 0, 1, 1, EnemyType.FAST);

        assertEquals(0f, enemy.angle, 0.0001f);
        assertEquals(0f, enemy.rotation, 0.0001f);
    }

    @Test
    void projectileDataShouldStoreConstructorValues() {
        final ProjectileData projectile =
                new ProjectileData(5f, 6f, "Interferon", 1.5f);

        assertEquals(5f, projectile.x, 0.0001f);
        assertEquals(6f, projectile.y, 0.0001f);
        assertEquals("Interferon", projectile.weaponName);
        assertEquals(1.5f, projectile.rotation, 0.0001f);
    }

    @Test
    void upgradeOptionShouldStoreConstructorValues() {
        final UpgradeOption option =
                new UpgradeOption("Velocita' aumentata", "Ti muovi piu' veloce", "⚡");

        assertEquals("Velocita' aumentata", option.name);
        assertEquals("Ti muovi piu' veloce", option.description);
        assertEquals("⚡", option.icon);
    }
}
