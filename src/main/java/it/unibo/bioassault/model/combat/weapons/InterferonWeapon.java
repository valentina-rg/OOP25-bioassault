package it.unibo.bioassault.model.combat.weapons;
/**
 * Arma alternativa con proiettili più lenti
 * ma con danno maggiore.
 */
    public class InterferonWeapon extends Weapon {
        /**
        * Crea un'arma interferone.
        */
        public InterferonWeapon() {
        super(20, 5.0f); // 20 danni, proiettile più lento
        }
    }