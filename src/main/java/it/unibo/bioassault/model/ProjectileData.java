package it.unibo.bioassault.model;

/**
 * Dati di un proiettile da passare alla View tramite GameSnapshot.
 * La View usa questi dati per disegnare il proiettile a schermo.
 */
public class ProjectileData {

    public float   x, y;       // posizione nel mondo
    public boolean isSpecial;  // true = proiettile speciale (stella a 4 punte)
    public float   rotation;   // angolo di rotazione per l'animazione

    public ProjectileData(final float x, final float y,
                          final boolean isSpecial, final float rotation) {
        this.x         = x;
        this.y         = y;
        this.isSpecial = isSpecial;
        this.rotation  = rotation;
    }
}
