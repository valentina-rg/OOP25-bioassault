package it.unibo.bioassault;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Carica immagini dal classpath.
 * Se la risorsa non esiste o la lettura fallisce, restituisce null
 * (invece di lanciare eccezioni), cosi' puo' attivare
 * un disegno di fallback.
 */
public class BufferedImageLoader {
    public BufferedImage loadImage(final String path) {
        try {
            final var url = getClass().getResource(path);
            if (url == null) {
                System.err.println("[Loader] Risorsa non trovata: " + path);
                return null;
            }
            return ImageIO.read(url);
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}