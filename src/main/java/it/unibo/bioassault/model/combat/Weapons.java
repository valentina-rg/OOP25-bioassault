package it.unibo.bioassault.model.combat;

/**
 * Classe base per tutte le armi del gioco.
 */
public abstract class Weapon {

    protected int damage; // Danno inflitto dall'arma

    // Costruttore dell'arma
    public Weapon(final int damage) {
        this.damage = damage; // Inizializza il danno dell'arma
    }

    // Restituisce il danno dell'arma
    public int getDamage() {
        return this.damage;
    }

    // Modifica il danno dell'arma
    public void setDamage(final int damage) {
        this.damage = damage;
    }
}