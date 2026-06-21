package it.unibo.bioassault.model.combat.weapons;

/**
 * Classe base per tutte le armi del gioco.
 */
public abstract class Weapon {

    protected int damage; // Danno inflitto dall'arma

    /**
     * Costruttore dell'arma.
     *
     * @param damage danno inflitto dall'arma
     */
    public Weapon(final int damage) {
        this.damage = damage; // Inizializza il danno dell'arma
    }

    /**
     * Restituisce il danno dell'arma.
     *
     * @return danno dell'arma
     */
    public int getDamage() {
        return this.damage; // Restituisce il danno corrente
    }

    /**
     * Modifica il danno dell'arma.
     *
     * @param damage nuovo danno dell'arma
     */
    public void setDamage(final int damage) {
        this.damage = damage; // Aggiorna il danno dell'arma
    }
}