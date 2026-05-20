package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.GameObject;

/**
 * Utility class for the basic combat logic involving viruses.
 * It centralizes simple collision and damage operations.
 */
public final class VirusCombatUtils {

    private VirusCombatUtils() {
    }

    /**
     * Checks a simplified square collision between a virus and another game object.
     *
     * @param virus the virus involved in the collision
     * @param object the other game object
     * @param virusSize the size of the virus hitbox
     * @param objectSize the size of the other object hitbox
     * @return true if the two hitboxes overlap
     */
    public static boolean collides(
            final Virus virus,
            final GameObject object,
            final float virusSize,
            final float objectSize
    ) {
        return virus.getX() < object.getX() + objectSize
                && virus.getX() + virusSize > object.getX()
                && virus.getY() < object.getY() + objectSize
                && virus.getY() + virusSize > object.getY();
    }

    /**
     * Applies damage to a virus.
     *
     * @param virus the virus that receives damage
     * @param damage the amount of damage
     */
    public static void applyDamage(final Virus virus, final int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        virus.hp = Math.max(0, virus.hp - damage);
    }

    /**
     * Checks whether a virus has no remaining HP.
     *
     * @param virus the virus to check
     * @return true if the virus is dead
     */
    public static boolean isDead(final Virus virus) {
        return virus.hp <= 0;
    }
}
