package it.unibo.bioassault.model.combat.collisions;

import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.player.Player;
import it.unibo.bioassault.model.viruses.Virus;

/**
 * Utility class che centralizza le collisioni
 * relative al sistema di combattimento.
 */
public final class CollisionSystem {

    private CollisionSystem() {
    }

    /**
     * Verifica se due oggetti di gioco sono in collisione.
     *
     * @param first primo oggetto
     * @param second secondo oggetto
     * @return true se le hitbox si intersecano
     */
    public static boolean collides(
            final GameObject first,
            final GameObject second
    ) {
        return first.getBounds().intersects(second.getBounds());
    }

    /**
     * Gestisce le collisioni tra il player e i virus.
     *
     * @param handler gestore degli oggetti
     * @param player player controllato dall'utente
     * @param damage danno applicato al player
     */
    public static void handlePlayerVirusCollisions(
            final Handler handler,
            final Player player,
            final int damage
    ) {
        for (int i = 0; i < handler.object.size(); i++) {
            final GameObject object = handler.object.get(i);

            if (object instanceof Virus
                    && CollisionSystem.collides(player, object)) {
                player.takeDamage(damage);
            }
        }
    }

    /**
     * Gestisce la collisione tra un proiettile e i virus.
     *
     * @param handler gestore degli oggetti
     * @param projectile proiettile da controllare
     * @param damage danno inflitto dal proiettile
     */
    public static void handleProjectileVirusCollisions(
            final Handler handler,
            final GameObject projectile,
            final int damage
    ) {

         if (damage < 0) {
            throw new IllegalArgumentException(
                    "Il danno non può essere negativo"
            );
        }
        
        for (int i = 0; i < handler.object.size(); i++) {
            final GameObject object = handler.object.get(i);

            if (object instanceof Virus virus
                    && CollisionSystem.collides(projectile, virus)) {
                virus.takeDamage(damage);
                handler.removeObject(projectile);
                return;
            }
        }
    }
}