package it.unibo.bioassault.controller;

import it.unibo.bioassault.model.*;
import it.unibo.bioassault.view.*;
import it.unibo.bioassault.model.combat.Projectile;
import it.unibo.bioassault.model.player.Player;
import it.unibo.bioassault.model.viruses.Virus;
import it.unibo.bioassault.model.viruses.types.Bacteria;
import it.unibo.bioassault.model.viruses.types.SpikyVirus;

import java.util.*;

/**
 * Costruisce un GameSnapshot leggendo solo l'API pubblica gia' esposta
 * da Handler/GameObject/Player/Virus.
 */
final class SnapshotBuilder {

    // maxHp non esiste come campo da nessuna parte nel model: memorizzo
    // il primo valore di hp visto per ogni istanza e lo uso come "massimo"
    // per la barra vita.
    private final Map<GameObject, Integer> initialHp = new WeakHashMap<>();

    GameSnapshot build(final Handler handler, final long startTimeMs) {
        final GameSnapshot snap = new GameSnapshot();

        final List<EnemyData> enemies = new ArrayList<>();
        final List<ProjectileData> projectiles = new ArrayList<>();
        Player playerRef = null;

        for (final GameObject obj : new ArrayList<>(handler.object)) {

            if (obj instanceof Player p) {
                final int maxHp = initialHp.computeIfAbsent(obj, o -> p.getHp());
                snap.playerX = p.getX() + 16;
                snap.playerY = p.getY() + 24;
                snap.playerHp = p.getHp();
                snap.playerMaxHp = Math.max(maxHp, p.getHp());
                playerRef = p;

            } else if (obj instanceof Virus v) {
                final EnemyType type =
                        (v instanceof SpikyVirus) ? EnemyType.BASIC
                      : (v instanceof Bacteria)   ? EnemyType.FAST
                      : EnemyType.FAST;
                enemies.add(new EnemyData(v.getX() + 16, v.getY() + 16,
                        v.getHp(), v.getMaxHp(), type));

            } else if (obj instanceof Projectile pr) {
                projectiles.add(new ProjectileData(pr.getX() + 4, pr.getY() + 4, pr.getWeaponName(), 0f));
            }
        }

        snap.enemies = enemies;
        snap.projectiles = projectiles;
        snap.xpOrbs = new ArrayList<>();
        snap.enemiesOnScreen = enemies.size();

        // Progressione: valori provvisori finche' il model non li implementa
        snap.level = 1;
        snap.xp = 0;
        snap.xpToNext = 100;
        snap.wave = 1;
        snap.isInvincible = false;

        snap.survivalSeconds = (int) ((System.currentTimeMillis() - startTimeMs) / 1000);
        snap.weapons = new String[] {playerRef.getWeapon().getName(), null, null, null, null, null};

        return snap;
    }
}
