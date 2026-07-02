package it.unibo.bioassault.model;

/**
 * Tipi di nemici presenti nel gioco.
 * Usato da EnemyData per dire all'ArenaPanel quale sprite/logica di disegno usare.
 */
public enum EnemyType {
    BASIC,  // virus base (SpikyVirus)
    FAST,   // virus veloce (Bacteria)
    ELITE   // nemico elite / boss
}
