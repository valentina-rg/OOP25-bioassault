package it.unibo.bioassault.model.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the character experience tracking
 */
public class ExperienceSystem {
    
    private static final int MAX_LEVEL = 100;
    private static final int UPGRADE_CHOICES = 3;
    private static final double XP_BASE = 100.0;
    private static final int STARTING_LEVEL = 1;
    private static final double XP_EXPONENT = 1.5;

    private int level = STARTING_LEVEL;
    private double currentXP = 0;
    private double xpToNextLevel;

    private final PlayerStats stats;
    private final List<Upgrade> upgradePool = new ArrayList<>();
    private final List<Upgrade> acquiredUpgrades = new ArrayList<>();
    private final List<LevelUpListener> listeners = new ArrayList<>();

    /**
     * Constructs a new ExperienceSystem bound to a specific character's stats.
     */
    public ExperienceSystem(PlayerStats stats) {
        this.stats = stats;
        this.xpToNextLevel = computeXpRequired(level);
        registerDefaultUpgrades();
    }

    private void registerDefaultUpgrades() {
        registerUpgrade(Upgrade.Preset.speedBoost());
        registerUpgrade(Upgrade.Preset.maxHpUp());
        registerUpgrade(Upgrade.Preset.damageUp());
        registerUpgrade(Upgrade.Preset.healOnPickup());
    }

    /**
     * Adds experience points to the system.
     *
     * @param amount is the amount of experience points earned by actions.
     */

    public void addXp(double amount) {
        if(level >= MAX_LEVEL) {
            return;
        }

        currentXP += amount;

        while (currentXP >= xpToNextLevel && level < MAX_LEVEL) {
            currentXP -= xpToNextLevel;
            level ++;
            xpToNextLevel = computeXpRequired(level);
            onLevelUp();
        }
    }

    /**.
     * Removes the chosen upgrade from the active pool if it cannot stack further.
     *
     * @param upgrade are the selected upgrades.
     */
    public void applyUpgrade(Upgrade upgrade) {
        upgrade.apply(stats);
        acquiredUpgrades.add(upgrade);
        upgradePool.remove(upgrade);
    }

    /**
     * Adds an upgrade into the pool available for selection upon leveling up.
     */
    public void registerUpgrade(Upgrade upgrade) {
        upgradePool.add(upgrade);
    }

    /**
     * Attach an external component to listen for leveling up events.
     */
    public void addLevelUpListener(LevelUpListener listener) {
        listeners.add(listener);
    }

    public int getLevel() { 
        return level; 
    }

    public double getCurrentXP() { 
        return currentXP; 
    }

    /** @return Total raw experience points threshold required to unlock the next level. */
    public double getXpToNextLevel() { 
        return xpToNextLevel; 
    }

    /** @return  progress percentage value*/
    public double getXpProgress() { 
        return currentXP / xpToNextLevel; 
    }

    public PlayerStats getStats() { 
        return stats; 
    }

    /** @return An unmodifiable view containing acquired upgrades. */
    public List<Upgrade> getAcquired() { 
        return List.copyOf(acquiredUpgrades); 
    }

    /**
     * Computes the experience threshold requirements using an exponential scaling algorithm.
     */
    private double computeXpRequired(int lvl) {
        return Math.floor(XP_BASE * Math.pow(lvl, XP_EXPONENT));
    }

    /**
     * Handles the execution logic triggered on level up
     */
    private void onLevelUp() {
        List<Upgrade> choices = pickRandomUpgrades(UPGRADE_CHOICES);
        listeners.forEach(l -> l.onLevelUp(level, choices));
    }

    /**
     * Randomly extracts a specific quantity of available upgrades from the upgrade pool 
     * without duplicating options
     */
    private List<Upgrade> pickRandomUpgrades(int count) {
        List<Upgrade> available = new ArrayList<> (upgradePool);
        List<Upgrade> chosen = new ArrayList<>(); 

        for(int i = 0; i < count && !available.isEmpty(); i ++) {
            int idx = (int) (Math.random() * available.size());
            chosen.add(available.remove(idx));
        }
        return chosen;
    }

    /**
     * Functional interface for listening to level up increments. 
     * Triggered immediately when a level benchmark has been cleared.
     */
    public interface LevelUpListener {
        void onLevelUp(int newLevel, List<Upgrade> upgradeChoices);
    }
}
