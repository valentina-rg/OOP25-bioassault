package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.viruses.types.BossVirus;

public class GenerateBoss implements GenerateVirus<Virus, Handler> {

    private static final float MIN_DISTANCE = 500.0f;
    private static final float MAX_DISTANCE = 600.0f;

    @Override
    public Virus createVirus(final Handler h) {
        final Virus v = new BossVirus(0, 0, ID.Enemy, h);
        v.setStartingPosition(MIN_DISTANCE, MAX_DISTANCE);
        return v;
    }
}