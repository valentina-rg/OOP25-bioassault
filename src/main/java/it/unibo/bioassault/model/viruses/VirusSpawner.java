package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Handler;

import java.util.Random;

public class VirusSpawner {

    private static final int TIME_LEVEL_1 = 20;
    private static final int BOSS_TIME = 45;

    private static final double SPAWN_RATE_SLOW = 0.5;
    private static final double SPAWN_RATE_MIN_PHASE2 = 1.0;
    private static final double SPAWN_RATE_MAX_PHASE2 = 2.0;
    private static final int RAMP_UP_STEP_SECONDS = 10;

    private final Handler handler;
    private double spawnRate = SPAWN_RATE_SLOW;

    private final long begin;
    private long lastSpawnTime;

    private boolean bossSpawned = false;

    private final GenerateSpike gen1;
    private final GenerateBacteria gen2;
    private final GenerateBoss genBoss;
    private final Random random = new Random();

    public VirusSpawner(final Handler h) {
        this.handler = h;
        this.gen1 = new GenerateSpike();
        this.gen2 = new GenerateBacteria();
        this.genBoss = new GenerateBoss();
        this.begin = System.currentTimeMillis();
        this.lastSpawnTime = this.begin;
    }

    public void spawnViruses() {
        final long currentTime = System.currentTimeMillis();
        final long elapsedTime = currentTime - lastSpawnTime;

        if (!bossSpawned && diff() >= BOSS_TIME) {
            spawnBoss();
            bossSpawned = true;
            return;
        }

        if (bossSpawned) {
            return;
        }

        updateSpawnRate();

        if (elapsedTime >= (long) (1000 / spawnRate)) {
            final Virus x = generateViruses();
            handler.addObject(x);
            lastSpawnTime = currentTime;
        }
    }

    public final void tick() {
        spawnViruses();
    }

    private void updateSpawnRate() {
        final int elapsed = diff();

        if (elapsed < TIME_LEVEL_1) {
            spawnRate = SPAWN_RATE_SLOW;
            return;
        }

        final int secondsIntoPhase2 = elapsed - TIME_LEVEL_1;
        final int increments = secondsIntoPhase2 / RAMP_UP_STEP_SECONDS;

        spawnRate = Math.min(
                SPAWN_RATE_MIN_PHASE2 + increments,
                SPAWN_RATE_MAX_PHASE2
        );
    }

    private Virus generateViruses() {
        if (diff() < TIME_LEVEL_1) {
            return gen1.createVirus(this.handler);
        }

        return random.nextBoolean()
                ? gen1.createVirus(this.handler)
                : gen2.createVirus(this.handler);
    }

    private void spawnBoss() {
        final Virus boss = genBoss.createVirus(this.handler);
        handler.addObject(boss);
    }

    final int diff() {
        return (int) ((System.currentTimeMillis() - this.begin) / 1000);
    }
}