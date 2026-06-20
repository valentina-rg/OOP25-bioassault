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
    private final list<Upgrade> upgradePool = new ArrayList<>();
    private final list<Upgrade> acquiredUpgrade = new ArrayList<>();
    private final list<LevelUpListener> listeners = new ArrayList<>();

    public ExperienceSystem() {
        
    }


}
