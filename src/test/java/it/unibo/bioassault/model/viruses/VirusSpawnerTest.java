package it.unibo.bioassault.model.viruses;

import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.bioassault.model.viruses.types.Bacteria;
import it.unibo.bioassault.model.viruses.types.BossVirus;
import it.unibo.bioassault.model.viruses.types.SpikyVirus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.bioassault.model.Handler;

class VirusSpawnerTest {

    private Handler handler;
    private VirusSpawner spawner;

    @BeforeEach
    void setUp() {
        handler = new Handler();
        spawner = new VirusSpawner(handler);
    }

    @Test
    void shouldSpawnBossAfterFinalThreshold() throws InterruptedException {
        Thread.sleep(56000); // supera TIME_LEVEL_3 = 55
        spawner.spawnViruses();

        boolean bossPresent = handler.object.stream()
                .anyMatch(obj -> obj instanceof BossVirus);

        assertTrue(bossPresent, "Dopo TIME_LEVEL_3 dovrebbe esserci almeno un virus big");

    }

    @Test
    void shouldSpawnAllExpectedVirusesIncludingBoss() throws InterruptedException {
        for (int i = 0; i < 60; i++) {
            Thread.sleep(1000);
            spawner.spawnViruses();
        }

        final boolean spikePresent = handler.object.stream()
                .anyMatch(obj -> obj instanceof SpikyVirus);

        final boolean bacteriaPresent = handler.object.stream()
                .anyMatch(obj -> obj instanceof Bacteria);

        final boolean bossPresent = handler.object.stream()
                .anyMatch(obj -> obj instanceof BossVirus);

        assertTrue(spikePresent, "Dovrebbe esserci almeno uno SpikeVirus");
        assertTrue(bacteriaPresent, "Dovrebbe esserci almeno uno BacteriaVirus");
        assertTrue(bossPresent, "Dovrebbe esserci il BossVirus finale");
    }
}

