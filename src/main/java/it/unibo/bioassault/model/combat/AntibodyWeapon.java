package it.unibo.bioassault.model.combat;

import it.unibo.bioassault.model.combat.weapons.Weapon;

/**
 * Arma che spara anticorpi contro i virus.
 */
public class AntibodyWeapon extends Weapon {

    public AntibodyWeapon() {
        super(10); // L'anticorpo infligge 10 danni
    }
}