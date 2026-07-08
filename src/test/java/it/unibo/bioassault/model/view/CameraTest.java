package it.unibo.bioassault.model.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics;
import java.awt.Rectangle;

import org.junit.jupiter.api.Test;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.view.Camera;

class CameraTest {

    private static class DummyGameObject extends GameObject {
        // TODO spawno la camera, spawno n gameobjects, controllo se dopo un tick di camera, gli sprite di tutti gli oggetti dentro lo schermo vengono caricati.
        // TODO Controllo anche che quelli fuori dallo schermo non vengano caricati.
        DummyGameObject(int x, int y) {
            super(x, y, null);
        }

        @Override
        public void tick() { }

        @Override
        public void render(Graphics g) { }

        @Override
        public Rectangle getBounds() {
            return new Rectangle((int) getX(), (int) getY(), 1, 1);
        }
    }

    @Test
    void cameraShouldMoveTowardsPlayer() {
        Camera camera = new Camera(0, 0);
        GameObject player = new DummyGameObject(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);

        camera.tick(player);

        assertTrue(camera.getX() > 0);
        assertTrue(camera.getY() > 0);
    }

    @Test
    void cameraShouldBeClampedToZero() {
        Camera camera = new Camera(0, 0);
        GameObject player = new DummyGameObject(0, 0);

        camera.tick(player);

        assertEquals(0f, camera.getX(), 0.0001f);
        assertEquals(0f, camera.getY(), 0.0001f);
    }

    @Test
    void cameraShouldBeClampedToWorldBounds() {
        Camera camera = new Camera(Game.WORLD_WIDTH, Game.WORLD_HEIGHT);
        GameObject player = new DummyGameObject(Game.WORLD_WIDTH, Game.WORLD_HEIGHT);

        camera.tick(player);

        assertEquals(Game.WORLD_WIDTH - Game.WINDOW_WIDTH, camera.getX(), 0.0001f);
        assertEquals(Game.WORLD_HEIGHT - Game.WINDOW_HEIGHT, camera.getY(), 0.0001f);
    }
}