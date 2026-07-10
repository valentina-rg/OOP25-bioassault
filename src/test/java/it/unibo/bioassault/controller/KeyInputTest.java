package it.unibo.bioassault.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Container;
import java.awt.event.KeyEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.bioassault.model.Handler;

class KeyInputTest {

    private Handler handler;
    private KeyInput keyInput;
    // Componente che serve solo come "source" per costruire i KeyEvent
    private final Container dummySource = new Container();

    @BeforeEach
    void setUp() {
        handler = new Handler();
        keyInput = new KeyInput(handler);
    }

    private KeyEvent press(final int keyCode) {
        return new KeyEvent(dummySource, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
    }

    private KeyEvent release(final int keyCode) {
        return new KeyEvent(dummySource, KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
    }

    @Test
    void wasdShouldSetMovementFlags() {
        keyInput.keyPressed(press(KeyEvent.VK_W));
        keyInput.keyPressed(press(KeyEvent.VK_A));
        keyInput.keyPressed(press(KeyEvent.VK_S));
        keyInput.keyPressed(press(KeyEvent.VK_D));

        assertTrue(handler.isUp());
        assertTrue(handler.isLeft());
        assertTrue(handler.isDown());
        assertTrue(handler.isRight());
    }

    @Test
    void arrowKeysShouldSetMovementFlags() {
        keyInput.keyPressed(press(KeyEvent.VK_UP));
        keyInput.keyPressed(press(KeyEvent.VK_LEFT));
        keyInput.keyPressed(press(KeyEvent.VK_DOWN));
        keyInput.keyPressed(press(KeyEvent.VK_RIGHT));

        assertTrue(handler.isUp());
        assertTrue(handler.isLeft());
        assertTrue(handler.isDown());
        assertTrue(handler.isRight());
    }

    @Test
    void releaseShouldResetMovementFlags() {
        keyInput.keyPressed(press(KeyEvent.VK_W));
        keyInput.keyPressed(press(KeyEvent.VK_D));

        keyInput.keyReleased(release(KeyEvent.VK_W));
        keyInput.keyReleased(release(KeyEvent.VK_D));

        assertFalse(handler.isUp());
        assertFalse(handler.isRight());
    }

    @Test
    void qShouldToggleSwitchWeaponFlag() {
        keyInput.keyPressed(press(KeyEvent.VK_Q));
        assertTrue(handler.isSwitchWeapon());

        keyInput.keyReleased(release(KeyEvent.VK_Q));
        assertFalse(handler.isSwitchWeapon());
    }

    @Test
    void unknownKeyShouldNotChangeAnyFlag() {
        keyInput.keyPressed(press(KeyEvent.VK_P));

        assertFalse(handler.isUp());
        assertFalse(handler.isDown());
        assertFalse(handler.isLeft());
        assertFalse(handler.isRight());
        assertFalse(handler.isSwitchWeapon());
    }

    @Test
    void oppositeKeysCanBothBePressed() {
        // KeyInput registra solo lo stato dei tasti
        keyInput.keyPressed(press(KeyEvent.VK_W));
        keyInput.keyPressed(press(KeyEvent.VK_S));

        assertTrue(handler.isUp());
        assertTrue(handler.isDown());
    }
}
