package it.unibo.bioassault.view;

import java.util.List;

/**
 * "Fotografia" dello stato di gioco in un determinato frame.
 * Viene creata dal Model (o dal Controller) e passata alla View
 * per il rendering, senza che la View debba accedere direttamente
 * agli oggetti di gioco.
 *
 * Tutti i campi sono pubblici per semplicita' di accesso dalla View,
 * ma nessuno li deve modificare dall'esterno (solo il Controller li imposta).
 */
public class GameSnapshot {

    // ---- Posizione e stato del giocatore ----
    public float playerX;
    public float playerY;
    public int   playerHp;
    public int   playerMaxHp;
    public boolean isInvincible; // true durante i frame di invincibilita' dopo un colpo

    // ---- Progressione ----
    public int level;
    public int xp;
    public int xpToNext;         // XP necessaria per salire di livello
    public int survivalSeconds;  // secondi totali di sopravvivenza

    // ---- Ondate ----
    public int wave;             // numero ondata corrente
    public int enemiesOnScreen;  // quanti nemici sono attualmente a schermo

    // ---- Entita' in gioco ----
    public List<EnemyData>      enemies;
    public List<ProjectileData> projectiles;
    public List<float[]>        xpOrbs; // ogni orb e' un array {x, y}

    // ---- Armi attive (null se lo slot e' vuoto) ----
    public String[] weapons;

    // Costruttore vuoto: i campi si impostano direttamente
    public GameSnapshot() { }
}
