package it.unibo.bioassault.model.player;

import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test relativi alla gestione degli HP e delle armi del player.
 */
class PlayerTest {

    /**
     * Verifica che il danno riduca gli HP del player
     * senza permettere valori inferiori a zero.
     */
    @Test
    void takeDamageShouldReduceHpAndNotGoBelowZero() {
        final Player player = new Player(
                0,
                0,
                ID.Player,
                new Handler()
        );

        assertEquals(100, player.getHp());
        assertFalse(player.isDead());

        player.takeDamage(30);

        assertEquals(70, player.getHp());
        assertFalse(player.isDead());

        player.takeDamage(100);

        assertEquals(0, player.getHp());
        assertTrue(player.isDead());
    }

    /**
     * Verifica che l'applicazione di un danno negativo
     * provochi un'eccezione.
     */
    @Test
    void negativeDamageShouldThrowException() {
        final Player player = new Player(
                0,
                0,
                ID.Player,
                new Handler()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> player.takeDamage(-1)
        );
    }

    /**
     * Verifica che il cambio arma avvenga una sola volta
     * per ogni pressione del comando dedicato.
     */
    @Test
    void weaponShouldChangeOnlyOnceForEachPress() {
        final Handler handler = new Handler();
        final Player player = new Player(
                0,
                0,
                ID.Player,
                handler
        );

        // Il player inizia con Antibody.
        assertEquals(
                "Antibody",
                player.getWeapon().getName()
        );

        // Prima pressione: passa a Interferon.
        handler.setSwitchWeapon(true);
        player.tick();

        assertEquals(
                "Interferon",
                player.getWeapon().getName()
        );

        /*
         * Il comando resta premuto:
         * l'arma non deve cambiare nuovamente.
         */
        player.tick();

        assertEquals(
                "Interferon",
                player.getWeapon().getName()
        );

        // Il comando viene rilasciato.
        handler.setSwitchWeapon(false);
        player.tick();

        // Una nuova pressione fa tornare ad Antibody.
        handler.setSwitchWeapon(true);
        player.tick();

        assertEquals(
                "Antibody",
                player.getWeapon().getName()
        );
    }
}