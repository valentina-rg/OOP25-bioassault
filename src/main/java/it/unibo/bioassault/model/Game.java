package it.unibo.bioassault.model;

import it.unibo.bioassault.BufferedImageLoader;
import it.unibo.bioassault.control.KeyInput;
import it.unibo.bioassault.model.player.Player;
import it.unibo.bioassault.model.viruses.Virus;
import it.unibo.bioassault.view.Camera;
import it.unibo.bioassault.view.Window;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import static it.unibo.bioassault.model.ID.Enemy;


public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    private boolean isRunning = false;
    private Thread thread;
    private Handler handler;
    private Camera camera;
    private BufferedImage level = null;

    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 563;

    public static final int WORLD_WIDTH = 2000;   // o quello che è
    public static final int WORLD_HEIGHT = 2000;  // o quello che è

    private int initialEnemies = 10; // quanti nemici iniziali


    public Game() {
        new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "BioAssault", this);
        start();



        handler = new Handler();
        camera = new Camera(0, 0);
        //handler.addObject(new Box(100, 100, ID.Enemy)); //qui chiariamo che l'oggetto è il nemico, non il giocatore
        this.addKeyListener(new KeyInput(handler));

        BufferedImageLoader loader = new BufferedImageLoader();
        level = loader.loadImage("/background/level2.png");
        //loadLevel(level);


        handler.addObject(new Player(100, 100, ID.Player, handler));
        handler.addObject(new Virus(600, 300, ID.Enemy, handler));

    }

    private void start(){
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop(){
        isRunning = false;
        try{
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void run() {
        this.requestFocus();
        double amountOfTicks = 60.0;
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        double ns = 1000000000 / amountOfTicks;
        int frames = 0;

        while (isRunning) {
            final long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta > 1) {
                tick();
                delta -= 1;
                // frames++;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }
        }
        stop();
    }

    public void tick() {
        // 1) aggiorno tutti gli oggetti (player compreso)
        handler.tick();

        // 2) trovo il player aggiornato e faccio seguire la camera
        for (int i = 0; i < handler.object.size(); i++) {
            if (handler.object.get(i).getId() == ID.Player) {
                camera.tick(handler.object.get(i));
            }
        }
    }


    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.black);
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        g2d.translate(-camera.getX(), -camera.getY());

        if (level != null) {
            g.drawImage(level, 0, 0, null);
        } else {
            g.setColor(Color.darkGray);
            g.fillRect(0, 0, Game.WORLD_WIDTH, Game.WORLD_HEIGHT);
        }

        handler.render(g);

        // DISATTIVA LA CAMERA
        g2d.translate(camera.getX(), camera.getY());

        g.dispose();
        bs.show();
    }

    private void loadLevel(BufferedImage image){
        int w = image.getWidth();
        int h = image.getHeight();

        for(int xx= 0; xx < w; xx++){
            for(int yy=0; yy <h; yy++){
                int pixel = image.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >>8) & 0xff;
                int blue = (pixel) & 0xff;

                if(blue == 255){
                    handler.addObject(new Player(xx*32, yy*32, ID.Player, handler));

                }

                if(red == 0 && green == 255 && blue == 0){
                    handler.addObject(new Virus(xx, yy, Enemy, handler));
                }

            }
        }
    }

}
