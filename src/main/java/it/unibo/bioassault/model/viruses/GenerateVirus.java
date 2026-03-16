package it.unibo.bioassault.model.viruses;

/**
 * interface which models creation of evils entities.
 *
 * @param <O> Output: returned created Object
 * @param <I> Handler input
 */
// ERRORE: Mancavano <O, I> qui accanto al nome!
public interface GenerateVirus<O, I> {

    /**
     * Method to create viruses.
     *
     * @param i Handler input
     * @return Output
     */
    O createVirus(I i); // Meglio chiamarlo createVirus invece di createMonster
}