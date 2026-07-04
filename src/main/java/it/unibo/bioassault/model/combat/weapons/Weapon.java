package it.unibo.bioassault.model.combat.weapons;

/**
 * Classe base per tutte le armi del gioco.
 */
public abstract class Weapon {

    protected int damage; // Danno inflitto dall'arma
    protected float projectileSpeed; // Velocità del proiettile sparato dall'arma

    public Weapon(final int damage, final float projectileSpeed) {
        this.damage = damage; // Inizializza il danno dell'arma
        this.projectileSpeed = projectileSpeed; // Inizializza la velocità del proiettile
    }

    public int getDamage() {
        return this.damage; // Restituisce il danno corrente
    }

    public float getProjectileSpeed() {
        return this.projectileSpeed; // Restituisce la velocità del proiettile
    }
}