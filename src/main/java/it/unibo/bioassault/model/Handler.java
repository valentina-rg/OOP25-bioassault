package it.unibo.bioassault.model;

import it.unibo.bioassault.model.stats.ExperienceSystem;
import it.unibo.bioassault.model.stats.PlayerStats;
import it.unibo.bioassault.model.stats.RunStats;

import java.awt.*;
import java.util.LinkedList;

public class Handler {

    public LinkedList<GameObject> object = new LinkedList<GameObject>();//array di oggetti


    private boolean up = false, down = false, right = false, left = false;


    private final PlayerStats playerStats = new PlayerStats();
    private final RunStats runStats = new RunStats();
    private final ExperienceSystem experienceSystem = new ExperienceSystem(playerStats);

    public PlayerStats getPlayerStats() { 
        return playerStats; 
    }

    public RunStats getRunStats() { 
        return runStats; 
    }

    public ExperienceSystem getExperienceSystem() { 
        return experienceSystem; 
    }

    public void tick(){
        //loop che cicla su tutti gli oggetti di gioco
        for(int i = 0; i < object.size(); i++){
            GameObject tempObject = object.get(i);

            tempObject.tick();
        }
    }

    public void render(Graphics g){
        for(int i = 0; i < object.size(); i++){
            GameObject tempObject = object.get(i);

            tempObject.render(g);
        }
    }

    public void addObject(GameObject tempObject){
        object.add(tempObject);
    }

    public void removeObject(GameObject tempObject){
        object.remove(tempObject);
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

}
