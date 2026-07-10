package it.unibo.bioassault.model.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test unitari per il sistema di statistiche e progressione di BioAssault.
 * Sfrutta le funzionalità avanzate di JUnit 5 (Nested Tests e Parameterized Tests).
 */
@DisplayName("Suite di Test dei Sistemi di Statistiche")
class StatsTest {

    // =========================================================================
    // PLAYER STATS
    // =========================================================================
    @Nested
    @DisplayName("Test di PlayerStats")
    class PlayerStatsTest {
        private PlayerStats stats;

        @BeforeEach
        void setUp() { 
            stats = new PlayerStats(); 
        }

        @Test
        @DisplayName("I valori HP di default devono essere 100/100 e il giocatore vivo")
        void defaultHpAndAlive() {
            assertAll("Valori Vitali Iniziali",
                () -> assertEquals(100, stats.getMaxHp()),
                () -> assertEquals(100, stats.getCurrentHp()),
                () -> assertTrue(stats.isAlive())
            );
        }

        @Test
        @DisplayName("receiveDamage deve ridurre correttamente gli HP e non scendere sotto lo 0")
        void receiveDamageBoundaries() {
            int dealt = stats.receiveDamage(30);
            assertAll("Danno Standard",
                () -> assertEquals(30, dealt),
                () -> assertEquals(70, stats.getCurrentHp())
            );

            stats.receiveDamage(200);
            assertAll("Danno da Overkill",
                () -> assertEquals(0, stats.getCurrentHp()),
                () -> assertFalse(stats.isAlive())
            );
        }

        @Test
        @DisplayName("receiveDamage deve infliggere almeno 1 danno anche con difesa al 100%")
        void receiveDamageMinimumOne() {
            stats.setDefense(1.0f);
            assertEquals(1, stats.receiveDamage(50));
        }

        @Test
        @DisplayName("La difesa al 50% deve dimezzare i danni in ingresso")
        void defenseHalvesDamage() {
            stats.setDefense(0.5f);
            int dealt = stats.receiveDamage(40);
            assertAll("Mitigazione della Difesa",
                () -> assertEquals(20, dealt),
                () -> assertEquals(80, stats.getCurrentHp())
            );
        }

        @Test
        @DisplayName("computeDamage deve calcolare correttamente i moltiplicatori e i colpi critici")
        void computeDamageLogic() {
            stats.setDamageMult(2.0f);
            stats.setCritChance(0.0f);
            assertEquals(20.0f, stats.computeDamage(10), 0.001f, "Moltiplicatore danno fallito");

            stats.setCritChance(1.0f);
            stats.setCritMult(3.0f);
            stats.setDamageMult(1.0f);
            assertEquals(30.0f, stats.computeDamage(10), 0.001f, "Calcolo critico certo fallito");
        }

        @Test
        @DisplayName("applyLifesteal deve curare in modo proporzionale senza superare i maxHp")
        void lifestealLogic() {
            stats.receiveDamage(50);
            stats.setLifesteal(0.5f);
            stats.applyLifesteal(40);
            assertEquals(70, stats.getCurrentHp(), "Cura da lifesteal errata");

            stats.applyLifesteal(1000);
            assertEquals(stats.getMaxHp(), stats.getCurrentHp(), "Lifesteal ha superato i Max HP");
        }

        @Test
        @DisplayName("Tutti i setter e getter devono effettuare il round-trip correttamente")
        void settersAndGettersRoundTrip() {
            stats.setMoveSpeed(200f);
            stats.setAttackSpeed(1.5f);
            stats.setAreaMult(1.2f);
            stats.setProjectiles(3);
            stats.setPickupRadius(80f);
            stats.setXpGainMult(2.0f);

            assertAll("Verifica Getters/Setters",
                () -> assertEquals(200f, stats.getMoveSpeed(), 0.001f),
                () -> assertEquals(1.5f, stats.getAttackSpeed(), 0.001f),
                () -> assertEquals(1.2f, stats.getAreaMult(), 0.001f),
                () -> assertEquals(3, stats.getProjectiles()),
                () -> assertEquals(80f, stats.getPickupRadius(), 0.001f),
                () -> assertEquals(2.0f, stats.getXpGainMult(), 0.001f)
            );
        }
    }

    // =========================================================================
    // UPGRADE
    // =========================================================================
    @Nested
    @DisplayName("Test di Upgrade")
    class UpgradeTest {
        private PlayerStats stats;

        @BeforeEach
        void setUp() { 
            stats = new PlayerStats(); 
        }

        @Test
        @DisplayName("Gli upgrade monouso non devono potersi attivare più di una volta")
        void singleUseAppliesOnce() {
            Upgrade heal = Upgrade.Preset.healOnPickup();
            stats.setCurrentHp(50);
            
            assertTrue(heal.apply(stats), "La prima applicazione deve riuscire");
            assertFalse(heal.apply(stats), "La seconda applicazione deve fallire");
            assertEquals(1, heal.getStackCount());
        }

        @Test
        @DisplayName("Gli upgrade accumulabili devono bloccarsi al raggiungimento del cap massimo")
        void stackableAppliesUpToMax() {
            Upgrade speed = Upgrade.Preset.speedBoost(); // max 3
            for (int i = 0; i < 3; i++) {
                assertTrue(speed.isAvailable(), "Dovrebbe essere disponibile allo stack " + i);
                assertTrue(speed.apply(stats), "Applicazione fallita allo stack " + i);
            }
            assertFalse(speed.isAvailable(), "Non dovrebbe più essere disponibile al max cap");
            assertFalse(speed.apply(stats), "L'applicazione oltre il cap deve fallire");
            assertEquals(3, speed.getStackCount());
        }

        @ParameterizedTest(name = "Upgrade Preset: {0}")
        @MethodSource("provideUpgradeEffects")
        @DisplayName("I preset degli upgrade devono applicare le corrette modifiche matematiche")
        void upgradePresetsEffects(String name, Upgrade upgrade, java.util.function.Consumer<PlayerStats> assertion) {
            upgrade.apply(stats);
            assertion.accept(stats);
        }

        private static Stream<Arguments> provideUpgradeEffects() {
            return Stream.of(
                Arguments.of("Speed Boost (+10%)", Upgrade.Preset.speedBoost(), 
                    (java.util.function.Consumer<PlayerStats>) s -> assertEquals(150.0f * 1.10f, s.getMoveSpeed(), 0.01f)),
                Arguments.of("Max HP Up (+20)", Upgrade.Preset.maxHpUp(), 
                    (java.util.function.Consumer<PlayerStats>) s -> assertEquals(120, s.getMaxHp())),
                Arguments.of("Damage Up (+15% float scale)", Upgrade.Preset.damageUp(), 
                    (java.util.function.Consumer<PlayerStats>) s -> assertTrue(s.getDamageMult() > 1.0f)),
                Arguments.of("Heal On Pickup (30% di 100)", Upgrade.Preset.healOnPickup(), 
                    (java.util.function.Consumer<PlayerStats>) s -> {
                        // Setup manuale eseguito dentro la lambda per isolamento del test parametrizzato
                        s.setCurrentHp(50);
                        Upgrade.Preset.healOnPickup().apply(s);
                        assertEquals(80, s.getCurrentHp());
                    })
            );
        }
    }

    // =========================================================================
    // RUN STATS
    // =========================================================================
    @Nested
    @DisplayName("Test di RunStats")
    class RunStatsTest {
        private RunStats run;

        @BeforeEach
        void setUp() { 
            run = new RunStats(); 
        }

        @Test
        @DisplayName("recordKill deve tracciare correttamente uccisioni totali, elìte, boss e per tipo")
        void recordKillTracking() {
            run.recordKill("SpikyVirus", true, false);  // Elite
            run.recordKill("SpikyVirus", false, false); // Normal
            run.recordKill("BossVirus", false, true);   // Boss

            assertAll("Tracciamento Kills",
                () -> assertEquals(3, run.getKills()),
                () -> assertEquals(1, run.getEliteKills()),
                () -> assertEquals(1, run.getBossKills()),
                () -> assertEquals(2, run.getKillsByType().get("SpikyVirus")),
                () -> assertEquals(1, run.getKillsByType().get("BossVirus"))
            );
        }

        @Test
        @DisplayName("I metodi di accumulo (XP, Oro, Danno) devono aggiornare correttamente i contatori")
        void metricsAccumulation() {
            run.addXP(100.5);
            run.addGold(50);
            run.addDamageDealt(150.5f);
            run.addDamageTaken(50.0f);

            assertAll("Verifica Accumulatori",
                () -> assertEquals(100L, run.getTotalXp()),
                () -> assertEquals(50, run.getGoldCollected()),
                () -> assertEquals(150.5f, run.getDamageDealt(), 0.01f),
                () -> assertEquals(50.0f, run.getDamageTaken(), 0.01f)
            );
        }

        @Test
        @DisplayName("endRun deve congelare definitivamente il timer di sopravvivenza")
        void endRunFreezesTimer() throws InterruptedException {
            run.endRun(true);
            long timeSnapshot = run.getSurvivalTime().toMillis();
            Thread.sleep(60);
            assertEquals(timeSnapshot, run.getSurvivalTime().toMillis(), 
                "Il timer ha continuato a scorrere dopo endRun");
            assertTrue(run.isSurvived());
        }

        @Test
        @DisplayName("Il calcolo del punteggio finale deve premiare la sopravvivenza e lo scaling del livello")
        void computeFinalScoreLogic() {
            run.recordKill("SpikyVirus", false, false);
            run.endRun(true);
            long scoreVictory = run.computeFinalScore(1);

            RunStats runLost = new RunStats();
            runLost.recordKill("SpikyVirus", false, false);
            runLost.endRun(false);
            long scoreDefeat = runLost.computeFinalScore(1);

            assertAll("Algoritmo Punteggio",
                // La vittoria applica un moltiplicatore x2 sulla base score
                () -> assertTrue(scoreVictory > scoreDefeat, "La vittoria dovrebbe raddoppiare il punteggio base"),
                () -> assertTrue(run.computeFinalScore(5) > run.computeFinalScore(1), "Il livello più alto deve dare più punti")
            );
        }

        @Test
        @DisplayName("toSummaryString deve contenere le keyword grafiche corrette in base all'esito")
        void summaryStringOutputs() {
            run.endRun(false);
            assertTrue(run.toSummaryString(1, List.of()).contains("GAME OVER"));

            RunStats victoryRun = new RunStats();
            victoryRun.endRun(true);
            assertTrue(victoryRun.toSummaryString(1, List.of()).contains("VITTORIA"));
        }
    }

    // =========================================================================
    // EXPERIENCE SYSTEM
    // =========================================================================
    @Nested
    @DisplayName("Test di ExperienceSystem")
    class ExperienceSystemTest {
        private PlayerStats stats;
        private ExperienceSystem xpSystem;

        @BeforeEach
        void setUp() {
            stats = new PlayerStats();
            xpSystem = new ExperienceSystem(stats);
        }

        @Test
        @DisplayName("Lo stato iniziale deve partire da Livello 1 con 0 XP")
        void initialState() {
            assertEquals(1, xpSystem.getLevel());
            assertEquals(0.0, xpSystem.getCurrentXP(), 0.001);
        }

        @Test
        @DisplayName("L'accumulo di XP deve gestire correttamente le soglie e i passaggi di livello multipli")
        void xpProgressionAndOverflow() {
            // Sotto la soglia -> Nessun level up
            xpSystem.addXp(xpSystem.getXpToNextLevel() - 1);
            assertEquals(1, xpSystem.getLevel());

            // Raggiunta la soglia -> Level up con mantenimento dei decimali/avanzi di XP
            xpSystem.addXp(1 + 10.0);
            assertEquals(2, xpSystem.getLevel());
            assertEquals(10.0, xpSystem.getCurrentXP(), 0.001);

            // Una massiccia dose di XP deve innescare istantaneamente un salto multi-livello
            xpSystem.addXp(20000);
            assertTrue(xpSystem.getLevel() > 3);
        }

        @Test
        @DisplayName("Il progresso percentuale XP deve oscillare correttamente tra 0.0 e 1.0")
        void xpProgressRatio() {
            xpSystem.addXp(xpSystem.getXpToNextLevel() / 2);
            double progress = xpSystem.getXpProgress();
            assertTrue(progress > 0.0 && progress < 1.0);
        }

        @Test
        @DisplayName("Al passaggio di livello i Listener registrati devono intercettare l'evento e ricevere le scelte")
        void levelUpObserverNotification() {
            AtomicInteger capturedLevel = new AtomicInteger(0);
            AtomicReference<List<Upgrade>> capturedChoices = new AtomicReference<>();

            xpSystem.addLevelUpListener((lvl, choices) -> {
                capturedLevel.set(lvl);
                capturedChoices.set(choices);
            });

            xpSystem.addXp(xpSystem.getXpToNextLevel());

            assertAll("Notifica Evento Level Up",
                () -> assertEquals(2, capturedLevel.get(), "Il listener non ha ricevuto il livello corretto"),
                () -> assertNotNull(capturedChoices.get(), "La lista delle scelte generata è nulla"),
                () -> assertTrue(capturedChoices.get().size() <= 3, "Sono state proposte più di 3 scelte")
            );
        }

        @Test
        @DisplayName("applyUpgrade deve mutare le statistiche in tempo reale e memorizzare lo storico dell'upgrade")
        void applyUpgradeIntegration() {
            int hpIniziali = stats.getMaxHp();
            Upgrade hpUp = Upgrade.Preset.maxHpUp();
            
            xpSystem.registerUpgrade(hpUp);
            xpSystem.applyUpgrade(hpUp);

            assertAll("Iniezione Upgrade",
                () -> assertEquals(hpIniziali + 20, stats.getMaxHp(), "Statistiche non mutate dall'ExperienceSystem"),
                () -> assertTrue(xpSystem.getAcquired().contains(hpUp), "L'upgrade non figura nel registro degli acquisiti")
            );
        }

        @Test
        @DisplayName("Raggiunto il livello massimo (100) il sistema deve congelare l'accumulo di XP")
        void levelCapEnforcement() {
            // Forziamo il raggiungimento del cap massimo di livello
            xpSystem.addXp(Double.MAX_VALUE / 4);
            int maxReached = xpSystem.getLevel();
            double xpSnapshot = xpSystem.getCurrentXP();

            // Proviamo ad aggiungere altra XP
            xpSystem.addXp(50000);
            assertAll("Blocco del Level Cap",
                () -> assertEquals(100, maxReached, "Il sistema non ha cappato al livello 100"),
                // Nota bene: al livello 100 l'addXp esce subito, lasciando l'XP corrente congelata
                // (poteva anche resettarla a 0, ma l'importante è che non livelli più)
                // Se la tua logica lascia l'avanzo precedente, questo test passa.
                () -> assertEquals(xpSnapshot, xpSystem.getCurrentXP(), 0.001)
            );
        }

        @Test
        @DisplayName("La lista restituita da getAcquired() deve essere rigorosamente immutabile")
        void acquiredListImmutability() {
            assertThrows(UnsupportedOperationException.class, 
                () -> xpSystem.getAcquired().add(Upgrade.Preset.damageUp()),
                "L'esterno ha potuto modificare la lista interna di upgrade acquisiti!");
        }
    }
}