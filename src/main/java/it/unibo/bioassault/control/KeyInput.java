package it.unibo.bioassault.control;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.Handler;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    //private final Game game;
    private final Handler handler;

    public KeyInput(final Handler handler) {
        //this.game = game;
        this.handler = handler;
    }

    public void keyPressed(KeyEvent e){
        final int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                handler.setLeft(true);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                handler.setRight(true);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                handler.setUp(true);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                handler.setDown(true);
                break;
            //case KeyEvent.VK_P:
                //game.statePause();
                //break;
            //case KeyEvent.VK_G:
                //game.switchDebug();
                // if (game.isDebugMode()) {
                // view.showDebug(game.getGraphics());
                // }
                //break;
            default:
                break;
        }

    }

    public void keyReleased(KeyEvent e){
        final int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                handler.setLeft(false);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                handler.setRight(false);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                handler.setUp(false);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                handler.setDown(false);
                break;
            default:
                break;
        }
    }
}
