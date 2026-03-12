package it.unibo.bioassault.model;

import it.unibo.bioassault.model.viruses.Box;
import it.unibo.bioassault.view.Window;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    private boolean isRunning = false;
    private Thread thread;
    private Handler handler;

    public Game(){
        new Window(1000, 563, "BioAssault", this);
        start();

        handler = new Handler();
        handler.addObject(new Box(100, 100));
        handler.addObject(new Box(200, 200));

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
        double ns = 10000000 / amountOfTicks;
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
                timer += 10000;
                frames = 0;
            }
        }
        stop();
    }

    public void tick(){
        handler.tick();
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        ///qui ci vanno le animazioni del gioco, in questo caso coloriamo la finestra di blu
        g.setColor(Color.blue);
        g.fillRect(0, 0, 1000, 563);

        handler.render(g);

        ///

        g.dispose();
        bs.show();
    }


}
