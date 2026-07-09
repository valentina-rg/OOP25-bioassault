package it.unibo.bioassault.model.stats;

import java.util.function.Consumer;

/**
 * Represents a game upgrade, perk, or power-up that alters a player's statistics.
 * It utilizes a functional approach using Consumer to encapsulate 
 * mutations applied directly to a PlayerStats instance.
 * * Supports both single-use and multi-stackable upgrades with maximum caps.
 **/

public class Upgrade {

    private final String                name;
    private final String                description;
    private final Consumer<PlayerStats> effect;
    private int                         stackCount = 0;
    private final int                   maxStacks;

    /**
     * Factory method to create a non-stackable, single-use upgrade.
     *
     * @param name is the name of the upgrade.
     * @param description is an explanation of the upgrade's effect.
     * @param effect is the behavior mutating the player's stats.
     * @return A new single-use Upgrade instance.
     */
    public static Upgrade of(String name, String description, Consumer<PlayerStats> effect) {
        return new Upgrade(name, description, effect, 1);
    }

    /**
     * Factory method to create a stackable upgrade with a defined maximum limit.
     *
     * @param name is the name of the upgrade.
     * @param description is an explanation of the upgrade's effect.
     * @param effect is the behavior mutating the player's stats.
     * @param maxStacks is the maximum number of times this upgrade can be applied.
     * @return A new stackable Upgrade instance.
     */
    public static Upgrade stackable(String name, String description,
                                    Consumer<PlayerStats> effect, int maxStacks) {
        return new Upgrade(name, description, effect, maxStacks);
    }

    private Upgrade(String name, String description,
                    Consumer<PlayerStats> effect, int maxStacks) {
        this.name        = name;
        this.description = description;
        this.effect      = effect;
        this.maxStacks   = maxStacks;
    }

    /**
     * Applies the upgrade's effect to the provided player stats, 
     * provided the upgrade has not reached its maximum stack capacity.
     * @return TRUE if the upgrade was successfully applied; FALSE if it has reached max stacks.
     */
    public boolean apply(PlayerStats stats) {
        if (maxStacks > 0 && stackCount >= maxStacks) return false;
        effect.accept(stats);
        stackCount++;
        return true;
    }

    /**
     * Checks whether this upgrade can still accept more stacks.
     * @return TRUE if the upgrade can still be applied; FALSE if it is maxed out.
     */
    public boolean isAvailable() {
        return maxStacks == 0 || stackCount < maxStacks;
    }

    public String getName() { 
        return name; 
    }
    public String getDescription() { 
        return description; 
    }
    public int    getStackCount() { 
        return stackCount; 
    }

    public int    getMaxStacks() { 
        return maxStacks; 
    }

    @Override
    public String toString() {
        String stack = maxStacks > 1 ? String.format(" (%d/%d)", stackCount, maxStacks) : "";
        return name + stack + "  –  " + description;
    }

    /**
     * A collection of ready-to-use upgrades.
     * New game upgrades should be registered here
     */
    public static final class Preset {

        private Preset() {}

        /**
         * Creates a stackable movement speed boost (+10% per stack, max 3 stacks).
         */
        public static Upgrade speedBoost() {
            return Upgrade.stackable(
                "Velocità +",
                "Aumenta la velocità di movimento del 10%",
                stats -> stats.setMoveSpeed(stats. getMoveSpeed() * 1.10f),
                3
            );
        }

        /**
         * Creates a stackable maximum health extension (+20 HP per stack, max 5 stacks).
         */
        public static Upgrade maxHpUp() {
            return Upgrade.stackable(
                "Vita massima +",
                "Aumenta la vita massima di 20",
                stats -> stats.setMaxHp(stats.getMaxHp() + 20),
                5
            );
        }

        /**
         * Creates a stackable base damage boost (+15% per stack, max 5 stacks).
         */
        public static Upgrade damageUp() {
            return Upgrade.stackable(
                "Danno +",
                "Aumenta il danno base del 15%",
                stats -> stats.setDamageMult((int) (stats.getDamageMult() * 1.15f)),
                5
            );
        }

        /**
         * Creates an instant, non-stackable heal that recovers 30% of maximum health.
         */
        public static Upgrade healOnPickup() {
            return Upgrade.of(
                "Rigenerazione",
                "Ripristina il 30% della vita massima immediatamente",
                stats -> stats.setCurrentHp(
                    Math.min(stats.getCurrentHp() + (int)(stats.getMaxHp() * 0.30f),
                             stats.getMaxHp())
                )
            );
        }
    }
}