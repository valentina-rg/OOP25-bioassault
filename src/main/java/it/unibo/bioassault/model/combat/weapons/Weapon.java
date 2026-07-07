package it.unibo.bioassault.model.combat.weapons;

/**
 * Classe base per tutte le armi del gioco.
 */
public abstract class Weapon {

    private final int damage; 
    private final float projectileSpeed; 

    protected Weapon(final int damage, final float projectileSpeed) {
        this.damage = damage; 
        this.projectileSpeed = projectileSpeed; 
    }

    public int getDamage() {
        return this.damage; 
    }

    public float getProjectileSpeed() {
        return this.projectileSpeed; 
    }
}