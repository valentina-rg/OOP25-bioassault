package it.unibo.bioassault.controller;

import it.unibo.bioassault.model.Handler;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Classe che gestisce l'input da tastiera per il movimento del giocatore.
 * Estende KeyAdapter cosi' non devo implementare tutti i metodi dell'interfaccia,
 * ma solo quelli che mi servono (keyPressed e keyReleased).
 *
 * Quando un tasto viene premuto aggiorno i flag nell'Handler,
 * che poi vengono letti dal Player nel suo metodo tick().
 */
public class KeyInput extends KeyAdapter {

    // riferimento all'handler che contiene i flag di movimento
    private final Handler handler;

    public KeyInput(final Handler handler) {
        this.handler = handler;
    }

    /**
     * Viene chiamato automaticamente ogni volta che si preme un tasto.
     * Uso lo switch con pattern multipli per tenere il codice piu' compatto.
     * Ho aggiunto anche le frecce direzionali come alternativa a WASD.
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> handler.setUp(true);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> handler.setDown(true);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> handler.setLeft(true);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> handler.setRight(true);
            case KeyEvent.VK_Q -> handler.setSwitchWeapon(true);
            default -> { } // altri tasti: non faccio nulla
        }
    }

    /**
     * Viene chiamato quando il tasto viene rilasciato.
     * Resetto il flag corrispondente a false cosi' il player si ferma.
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> handler.setUp(false);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> handler.setDown(false);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> handler.setLeft(false);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> handler.setRight(false);
            case KeyEvent.VK_Q -> handler.setSwitchWeapon(false);
            default -> { }
        }
    }
}
