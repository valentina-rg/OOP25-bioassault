package it.unibo.bioassault.model.combat;

import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.player.Player;
import it.unibo.bioassault.model.viruses.Virus;

/**
 * Gestisce le collisioni principali del sistema di combattimento.
 */
public final class CollisionSystem {

    // Verifica se due oggetti di gioco sono in collisione.
    public static boolean collides(final GameObject first, final GameObject second) {
        return first.getBounds().intersects(second.getBounds()); // Controlla se le hitbox si sovrappongono.
    }

    // Gestisce la collisione tra player e virus.
    public static void handlePlayerVirusCollisions(
            final Handler handler,
            final Player player,
            final int damage
    ) {
        for (final GameObject object : handler.object) { // Scorre tutti gli oggetti presenti nel gioco.

            if (object instanceof Virus virus) { // Considera solo gli oggetti che sono virus.

                if (CollisionSystem.collides(player, virus)) { // Se player e virus si toccano.
                    player.takeDamage(damage); // Il player perde HP.
                }
            }
        }
    }

    // Gestisce la collisione tra un proiettile e i virus.
    public static void handleProjectileVirusCollisions(
            final Handler handler,
            final GameObject projectile,
            final int damage
    ) {
        for (final GameObject object : handler.object) { // Scorre tutti gli oggetti presenti nel gioco.

            if (object instanceof Virus virus) { // Considera solo gli oggetti che sono virus.

                if (CollisionSystem.collides(projectile, virus)) { // Se proiettile e virus si toccano.
                    virus.takeDamage(damage); // Il virus perde HP.
                    handler.removeObject(projectile); // Il proiettile viene rimosso dopo il colpo.
                    return; // Il proiettile colpisce un solo virus.
                }
            }
        }
    }
}