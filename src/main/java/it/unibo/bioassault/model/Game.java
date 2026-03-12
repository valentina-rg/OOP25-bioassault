package it.unibo.bioassault.model;

import it.unibo.bioassault.control.KeyInput;
import it.unibo.bioassault.model.player.Player;
import it.unibo.bioassault.model.viruses.Box;
import it.unibo.bioassault.view.Camera;
import it.unibo.bioassault.view.Window;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    private boolean isRunning = false;
    private Thread thread;
    private Handler handler;
    private Camera camera;

    public Game(){
        new Window(1000, 563, "BioAssault", this);
        start();



        handler = new Handler();
        camera = new Camera(0, 0);
        //handler.addObject(new Box(100, 100, ID.Enemy)); //qui chiariamo che l'oggetto è il nemico, non il giocatore
        this.addKeyListener(new KeyInput(handler));
        handler.addObject(new Player(100, 100, ID.Player, handler));
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

    public void tick(){

        for(int i = 0; i < handler.object.size(); i++){
            if(handler.object.get(i).getId() == ID.Player){
                camera.tick(handler.object.get(i));
            }
        }

        handler.tick();
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g; 
        ///qui ci vanno le animazioni del gioco, in questo caso coloriamo la finestra di blu

        g.setColor(Color.blue);
        g.fillRect(0, 0, 1000, 563);

        g2d.translate(-camera.getX(), -camera.getY());

        handler.render(g);

        g2d.translate(camera.getX(), camera.getY());



        ///

        g.dispose();
        bs.show();
    }


}
