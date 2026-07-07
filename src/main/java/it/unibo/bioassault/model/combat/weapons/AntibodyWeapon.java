package it.unibo.bioassault.model.combat.weapons;
/**
 * Arma base del player.
 * Produce proiettili veloci con danno moderato.
 */
    public class AntibodyWeapon extends Weapon {
        /**
        * Crea un'arma anticorpo.
        */
        public AntibodyWeapon() {
          super(30, 10.0f); // 30 danni, proiettile veloce
        }
    }