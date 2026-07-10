package it.unibo.bioassault.model.combat.weapons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test relativi alle configurazioni delle armi disponibili.
 */
class WeaponTest {

    /**
     * Verifica che le armi espongano correttamente
     * nome, danno e velocità del proiettile.
     */
    @Test
    void weaponsShouldExposeConfiguredValues() {
        final Weapon antibody = new AntibodyWeapon();
        final Weapon interferon = new InterferonWeapon();

        assertEquals("Antibody", antibody.getName());
        assertEquals(30, antibody.getDamage());
        assertEquals(
                10.0f,
                antibody.getProjectileSpeed(),
                0.0001f
        );

        assertEquals("Interferon", interferon.getName());
        assertEquals(42, interferon.getDamage());
        assertEquals(
                5.0f,
                interferon.getProjectileSpeed(),
                0.0001f
        );
    }
}