package main.java.it.unibo.bioassault.model.stats;

import java.util.ArrayList;
import java.util.List;

public class ExperienceSystem {
    
    private static final int MAX_LEVEL = 100;
    private static final int UPGRADE_CHOICES = 3;
    private static final double XP_BASE = 100.0;
    private static final int STARTING_LEVEL = 1;

    private int level = STARTING_LEVEL;
    private double currentXP = 0;
    private double xpToNextLevel;

    private final PlayerStats stats;
    private final List<Upgrade> upgradePool = new ArrayList<>();
    private final list<Upgrade> acquiredUpgrade = new ArrayList<>();
    private final list<LevelUpListener> listeners = new ArrayList<>();

    public ExperienceSystem(PlayerStats stats) {
        this.stats = stats;
        this.xpToNextLevel = computeXpRequired(level);
        registerDefaultUpgrades();
    }

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

    public void applyUpgrade(Upgrade upgrade) {
        upgrade.apply(stats);
        acquiredUpgrade.add(upgrade);
        upgradePool.remove(upgrade);
    }

    public void registerUpgrade(Upgrade upgrade) {
        upgradePool.add(upgrade);
    }

    public void addLevelUpListener(LevelUpListener listener) {
        listeners.add(listener);
    }

    public int getLevel() { 
        return level; 
    }

    public double getCurrentXP() { 
        return currentXP; 
    }

    public double getXpToNextLevel() { 
        return xpToNextLevel; 
    }

    public double getXpProgress() { 
        return currentXP / xpToNextLevel; 
    }  // 0.0 – 1.0

    public PlayerStats getStats() { 
        return stats; 
    }

    public List<Upgrade> getAcquired() { 
        return List.copyOf(acquiredUpgrades); 
    }

    private double computeXpRequired(int lvl) {
        return Math.floor(XP_BASE * Math.pow(lvl, XP_EXPONENT));
    }

    private void onLevelUP() {
        List<Upgrade> choices = pickRandomUpgrades(UPGRADE_CHOICES);
        listeners.forEach(l -> l.onLevelUP(level, choices));
    }

    private List<Upgrade> pickRandomUpgrades(int count) {
        List<Upgrade> available = new ArrayList<> (upgradePool);
        List<Upgrade> chosen = new ArrayList<>(); 

        for(int i = 0; i < count && !available.isEmpty(); i ++) {
            int idx = (int) (Math.random() * available.size());
            chosen.add(available.remove(idx));
        }
        return chosen;
    }
    public interface LevelUpListener {
        void onLevelUp(int newLevel, List<Upgrade> upgradeChoices);
    }
}
