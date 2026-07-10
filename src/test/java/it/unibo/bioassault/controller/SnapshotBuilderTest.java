package it.unibo.bioassault.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.combat.Projectile;
import it.unibo.bioassault.model.player.Player;
import it.unibo.bioassault.model.viruses.types.Bacteria;
import it.unibo.bioassault.model.viruses.types.SpikyVirus;
import it.unibo.bioassault.view.EnemyType;
import it.unibo.bioassault.view.GameSnapshot;

class SnapshotBuilderTest {

    private static final int PLAYER_X = 100;
    private static final int PLAYER_Y = 200;

    private Handler handler;
    private SnapshotBuilder builder;

    @BeforeEach
    void setUp() {
        handler = new Handler();
        builder = new SnapshotBuilder();
    }

    private Player addPlayer() {
        final Player player = new Player(PLAYER_X, PLAYER_Y, ID.Player, handler);
        handler.addObject(player);
        return player;
    }

    @Test
    void snapshotShouldContainPlayerPositionWithSpriteOffset() {
        addPlayer();

        final GameSnapshot snap = builder.build(handler, System.currentTimeMillis());

        // Il builder centra la posizione sullo sprite: +16 su x, +24 su y
        assertEquals(PLAYER_X + 16, snap.playerX, 0.0001f);
        assertEquals(PLAYER_Y + 24, snap.playerY, 0.0001f);
    }

    @Test
    void snapshotShouldContainPlayerHpAndWeapon() {
        final Player player = addPlayer();

        final GameSnapshot snap = builder.build(handler, System.currentTimeMillis());

        assertEquals(player.getHp(), snap.playerHp);
        assertEquals(player.getHp(), snap.playerMaxHp);
        // Il primo slot armi contiene l'arma equipaggiata (Antibody di default)
        assertEquals(player.getWeapon().getName(), snap.weapons[0]);
    }

    @Test
    void maxHpShouldBeRememberedAfterDamage() {
        final Player player = addPlayer();
        final int fullHp = player.getHp();

        // Primo snapshot: memorizza gli HP iniziali come massimo
        builder.build(handler, System.currentTimeMillis());

        player.takeDamage(30);
        final GameSnapshot snap = builder.build(handler, System.currentTimeMillis());

        assertEquals(fullHp - 30, snap.playerHp);
        assertEquals(fullHp, snap.playerMaxHp);
    }

    @Test
    void virusesShouldBecomeEnemyDataWithCorrectType() {
        addPlayer();
        handler.addObject(new SpikyVirus(300, 400, ID.Enemy, handler));
        handler.addObject(new Bacteria(500, 600, ID.Enemy, handler));

        final GameSnapshot snap = builder.build(handler, System.currentTimeMillis());

        assertEquals(2, snap.enemies.size());
        assertEquals(2, snap.enemiesOnScreen);
        // SpikyVirus -> BASIC, Bacteria -> FAST
        assertEquals(EnemyType.BASIC, snap.enemies.get(0).type);
        assertEquals(EnemyType.FAST, snap.enemies.get(1).type);
        // Posizione centrata sullo sprite: +16 su entrambe le coordinate
        assertEquals(300 + 16, snap.enemies.get(0).x, 0.0001f);
        assertEquals(400 + 16, snap.enemies.get(0).y, 0.0001f);
    }

    @Test
    void enemyDataShouldCarryVirusHp() {
        addPlayer();
        final SpikyVirus virus = new SpikyVirus(300, 400, ID.Enemy, handler);
        handler.addObject(virus);
        virus.takeDamage(40);

        final GameSnapshot snap = builder.build(handler, System.currentTimeMillis());

        assertEquals(virus.getHp(), snap.enemies.get(0).hp);
        assertEquals(virus.getMaxHp(), snap.enemies.get(0).maxHp);
    }

    @Test
    void projectilesShouldBecomeProjectileData() {
        addPlayer();
        handler.addObject(new Projectile(50, 60, handler, 1f, 0f, 10,
                "Antibody", ID.Projectile));

        final GameSnapshot snap = builder.build(handler, System.currentTimeMillis());

        assertEquals(1, snap.projectiles.size());
        assertEquals("Antibody", snap.projectiles.get(0).weaponName);
        assertEquals(50 + 4, snap.projectiles.get(0).x, 0.0001f);
        assertEquals(60 + 4, snap.projectiles.get(0).y, 0.0001f);
    }

    @Test
    void emptyHandlerWithPlayerShouldHaveNoEnemiesOrProjectiles() {
        addPlayer();

        final GameSnapshot snap = builder.build(handler, System.currentTimeMillis());

        assertTrue(snap.enemies.isEmpty());
        assertTrue(snap.projectiles.isEmpty());
        assertEquals(0, snap.enemiesOnScreen);
    }

    @Test
    void survivalSecondsShouldBeComputedFromStartTime() {
        addPlayer();

        // Partita iniziata 5 secondi fa
        final long fiveSecondsAgo = System.currentTimeMillis() - 5000;
        final GameSnapshot snap = builder.build(handler, fiveSecondsAgo);

        assertEquals(5, snap.survivalSeconds);
    }

    @Test
    void buildWithoutPlayerShouldFail() {
        assertThrows(NullPointerException.class,
                () -> builder.build(handler, System.currentTimeMillis()));
    }
}
