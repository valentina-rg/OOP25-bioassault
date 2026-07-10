package it.unibo.bioassault.model.viruses;

public interface IVirus {
    void takeDamage(int damage);
    int getHp();
    int getMaxHp();
    boolean isDead();
    void setIsBig(boolean isBig);
    void setStartingPosition(float minDistance, float maxDistance);
    void reachTarget();
}