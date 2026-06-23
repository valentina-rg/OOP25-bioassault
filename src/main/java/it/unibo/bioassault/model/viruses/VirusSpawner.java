package it.unibo.bioassault.model.viruses;

import java.util.stream.Stream;
import it.unibo.bioassault.model.Handler;



/**
 * Class to manage spawn of viruses.
 */
public class VirusSpawner {

    private static final int TIME_LEVEL_1 = 20;
    private static final int TIME_LEVEL_2 = 40;
    private static final int TIME_LEVEL_3 = 55;

    private final Handler handler;
    private static int spawnRate = 1;
    private static final int BOSS_SPAWN_TIME = 30;

    private final long begin;
    private long lastSpawnTime;
    private int currentSecond;
    private long currentTime;

    private final GenerateSpike gen1;
    private final GenerateBacteria gen2;

    //flag per il boss finale
    private boolean bossSpawned = false;

    /**
     * Inizialize virus creation factories.
     * * @param h handler
     */
    public VirusSpawner(final Handler h) {
        this.handler = h;
        this.gen1 = new GenerateSpike();
        this.gen2 = new GenerateBacteria();
        this.begin = System.currentTimeMillis() / 1000;
    }

    /**
     * Spawn viruses all around the player
     * Creation is time based.
     */
    public void spawnViruses() {
        long elapsedTime;

        currentTime = System.currentTimeMillis();
        currentSecond = (int) ((currentTime / 1000)); // update current second

        elapsedTime = currentTime - lastSpawnTime;

        if (diff() > TIME_LEVEL_1) {
            spawnRate = 1;
        }

        if (elapsedTime >= 1000 / spawnRate) {
            final Virus x = gen1.createVirus(this.handler);
            handler.addObject(x);
            lastSpawnTime = currentTime;

            //generateFixedPositionViruses();
        }

        if (!bossSpawned && diff() >= BOSS_SPAWN_TIME) {
            final Virus boss = gen2.createVirus(this.handler);
            boss.setIsBig(true);
            handler.addObject(boss);
            bossSpawned = true;

        }
    }

    public final void tick() {
        spawnViruses();
    }

    /**
     * create different type of viruses based on time.
     * * @return virus.
     */
    private Virus generateViruses() {

        currentSecond = (int) ((currentTime / 1000)); // update current second
        Virus v;

        if ((diff()) < TIME_LEVEL_1) {
            v = gen1.createVirus(this.handler);
        } else if (diff() == TIME_LEVEL_1) {
            v = gen1.createVirus(this.handler);
            v.setIsBig(true);
        } else if (diff() < TIME_LEVEL_2) {
            v = gen2.createVirus(this.handler);
        } else if (diff() == TIME_LEVEL_2) {
            v = gen2.createVirus(this.handler);
            v.setIsBig(true);
        } else {
            v = gen2.createVirus(this.handler);
        }

        return v;
    }

    /**
     * viruses created all through the game.
     */
    private void generateFixedPositionViruses() {
        final Virus rh = gen1.createVirus(this.handler);
        handler.addObject(rh);
    }

    /**
     * massive flood of viruses.
     */
    private void flood() {
        Stream.generate(() -> gen2.createVirus(this.handler))
                .limit(1)
                .forEach(v -> {
                    v.setIsBig(true);
                    handler.addObject(v);
                });
    }

    private void spawnBoss() {
        final Virus boss = gen2.createVirus(this.handler);
        boss.setIsBig(true); // HP x2, dimensione maggiore
        handler.addObject(boss);
    }

    /**
     * @return time in seconds from the begin of game
     */
    final int diff() {
        return (int) (currentSecond - this.begin);
    }
}