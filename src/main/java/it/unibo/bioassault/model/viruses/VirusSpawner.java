package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Handler;

import java.util.Random;

public class VirusSpawner {

    private static final int TIME_LEVEL_1 = 20; // fino a 20s: solo Spike, spawn dolce
    private static final int BOSS_TIME = 45;    // al secondo 45: spawn boss (una tantum)

    private static final int SPAWN_RATE_SLOW = 1;      // virus al secondo, fase 1 (solo Spike)
    private static final int SPAWN_RATE_MIN_PHASE2 = 2; // virus al secondo, inizio fase 2
    private static final int SPAWN_RATE_MAX_PHASE2 = 5; // virus al secondo, picco fase 2 (appena prima del boss)
    private static final int RAMP_UP_STEP_SECONDS = 7;  // ogni quanti secondi sale di 1 in fase 2

    private final Handler handler;
    private int spawnRate = SPAWN_RATE_SLOW;

    private final long begin;
    private long lastSpawnTime;
    private int currentSecond;
    private long currentTime;

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
        this.begin = System.currentTimeMillis() / 1000;
    }

    public void spawnViruses() {
        long elapsedTime;

        currentTime = System.currentTimeMillis();
        currentSecond = (int) (currentTime / 1000);

        elapsedTime = currentTime - lastSpawnTime;

        // Il boss è comparso: lo spawn normale si ferma definitivamente
        if (bossSpawned) {
            return;
        }

        updateSpawnRate();

        if (elapsedTime >= 1000 / spawnRate) {
            final Virus x = generateViruses();
            handler.addObject(x);
            lastSpawnTime = currentTime;
        }

        if (diff() >= BOSS_TIME) {
            spawnBoss();
            bossSpawned = true;
        }
    }

    public final void tick() {
        spawnViruses();
    }

    /**
     * Calcola il ritmo di spawn corrente in base al tempo trascorso.
     * Fase 1 (0-20s): ritmo fisso e dolce, solo Spike.
     * Fase 2 (20s-55s): il ritmo cresce gradualmente ogni RAMP_UP_STEP_SECONDS,
     * fino a un massimo di SPAWN_RATE_MAX_PHASE2, per un'aggressività crescente.
     */
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

    /**
     * Fase 1 (0-20s): solo Spike.
     * Fase 2 (20s+): Spike e Bacteria insieme, scelti a caso 50/50.
     *
     * @return virus.
     */
    private Virus generateViruses() {
        if (diff() < TIME_LEVEL_1) {
            return gen1.createVirus(this.handler);
        }

        return random.nextBoolean()
                ? gen1.createVirus(this.handler)
                : gen2.createVirus(this.handler);
    }

    /**
     * Spawna il boss finale
     */
    private void spawnBoss() {
        final Virus boss = genBoss.createVirus(this.handler);
        handler.addObject(boss);
    }

    final int diff() {
        return (int) (currentSecond - this.begin);
    }

   /* public int getCurrentWave() {
        if (diff() < TIME_LEVEL_1) {
            return 1;
        }
        if (diff() < TIME_LEVEL_2) {
            return 2;
        }
        if (diff() < TIME_LEVEL_3) {
            return 3;
        }
        return 4;
    }*/
}