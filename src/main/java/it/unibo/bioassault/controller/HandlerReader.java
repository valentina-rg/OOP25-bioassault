package it.unibo.bioassault.controller;
import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.Handler;

final class HandlerReader {

    private HandlerReader() { }

    static Handler extractHandler(final Game game) {
        return game.getHandler();
    }
}
