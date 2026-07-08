package it.unibo.bioassault.view;

/**
 * Dati di un nemico da passare alla View tramite GameSnapshot.
 * Contiene solo le informazioni necessarie al rendering,
 * non la logica di gioco (quella resta nel Model).
 */
public class EnemyData {

    public float     x, y;      // posizione nel mondo
    public int       hp;        // vita attuale
    public int       maxHp;     // vita massima (per la barra HP)
    public EnemyType type;      // tipo nemico -> determina come viene disegnato
    public float     angle;     // angolo di rotazione del corpo (usato per virus veloce)
    public float     rotation;  // angolo di rotazione aggiuntivo (proiettili speciali)

    public EnemyData(final float x, final float y,
                     final int hp, final int maxHp,
                     final EnemyType type) {
        this.x        = x;
        this.y        = y;
        this.hp       = hp;
        this.maxHp    = maxHp;
        this.type     = type;
        this.angle    = 0f;
        this.rotation = 0f;
    }
}
