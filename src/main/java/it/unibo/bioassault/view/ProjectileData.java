package it.unibo.bioassault.view;

/**
 * Dati di un proiettile da passare alla View tramite GameSnapshot.
 * La View usa questi dati per disegnare il proiettile a schermo.
 */
public class ProjectileData {

    public float   x, y;       // posizione nel mondo
    public String weaponName;  // nome dell'arma che ha sparato il proiettile (es. "Interferon")
    public float   rotation;   // angolo di rotazione per l'animazione

    public ProjectileData(final float x, final float y,
                          final String weaponName, final float rotation) {
        this.x         = x;
        this.y         = y;
        this.weaponName = weaponName;
        this.rotation  = rotation;
    }
}
