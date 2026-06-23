package it.unibo.bioassault.model.combat;

import it.unibo.bioassault.model.combat.weapons.Weapon;

/**
 * Arma che utilizza interferoni per colpire i virus.
 */
public class InterferonWeapon extends Weapon {

    public InterferonWeapon() {
        super(20); // L'interferone infligge 20 danni
    }
}