package it.unibo.bioassault.model.combat.weapons;

    /**
    * Classe base per tutte le armi del gioco.
    */
public abstract class Weapon {

    private final int damage; 
    private final float projectileSpeed; 
    
    /**
    * Crea una nuova arma.
     *
     * @param damage danno inflitto dal proiettile
     * @param projectileSpeed velocità del proiettile
     */

    protected Weapon(final int damage, final float projectileSpeed) {
        this.damage = damage; 
        this.projectileSpeed = projectileSpeed; 
    }

     /**
     * Restituisce il danno dell'arma.
     *
     * @return danno inflitto dal proiettile
     */

    public int getDamage() {
        return this.damage; 
    }

    /**
     * Restituisce la velocità del proiettile.
     *
     * @return velocità del proiettile
     */

    public float getProjectileSpeed() {
        return this.projectileSpeed; 
    }
    
}