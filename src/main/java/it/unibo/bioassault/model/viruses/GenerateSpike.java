package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.viruses.types.SpikyVirus;


public class GenerateSpike implements GenerateVirus<Virus, Handler> {

    private static final float MIN_DISTANCE = 500.0f; // default monster creation min radius (distance from player)
    private static final float MAX_DISTANCE = 600.0f; // default monster creation min radius (distance from player)

    @Override
    public Virus createVirus(final Handler h) {
        final Virus v = new SpikyVirus(0, 0, ID.Enemy, h);
        v.setStartingPosition(MIN_DISTANCE, MAX_DISTANCE);
        return v;
    }
}
