package it.unibo.bioassault.view;

import it.unibo.bioassault.BufferedImageLoader;
import it.unibo.bioassault.model.EnemyData;
import it.unibo.bioassault.model.GameSnapshot;
import it.unibo.bioassault.model.ProjectileData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Pannello principale di gioco: disegna l'arena biologica, il personaggio,
 * i nemici e i proiettili.
 *
 * La camera segue il giocatore mantenendolo al centro dello schermo.
 * Il rendering usa un buffer off-screen per evitare il flickering.
 *
 * Questo pannello non conosce la logica di gioco: riceve uno snapshot
 * (GameSnapshot) dal Controller e lo disegna, nulla di piu'.
 */
public class ArenaPanel extends JPanel {

    // ---- Dimensioni del mondo di gioco (devono corrispondere a Game.WORLD_WIDTH/HEIGHT) ----
    public static final int WORLD_W = 2000;
    public static final int WORLD_H = 2000;

    // ---- Colori usati nel rendering delle entita' ----
    // Nota: new Color(int, int) NON esiste in Java.
    // Per colori con trasparenza si usa new Color(r, g, b, alpha).
    private static final Color BG_DARK        = new Color(10, 26, 14);       // 0x0a1a0e
    private static final Color BORDER_COLOR   = new Color(0x2ecc71);         // verde pieno
    private static final Color BORDER_GLOW    = new Color(46, 204, 113, 60); // verde trasparente
    private static final Color PLAYER_COLOR   = new Color(0x55efc4);
    private static final Color PLAYER_NUCLEUS = new Color(0xffeaa7);
    private static final Color PROJ_COLOR     = new Color(0xfdcb6e);
    private static final Color XP_ORB_COLOR   = new Color(0xa29bfe);

    // ---- Sprite caricati da file PNG ----
    private BufferedImage backgroundImage  = null;
    private BufferedImage basicVirusSprite = null; // sprite-virus.png
    private BufferedImage bacteriaSprite   = null; // bacteria-sprite.png

    // ---- Snapshot corrente (aggiornato dal Controller ogni frame) ----
    private GameSnapshot snapshot = null;

    // ---- Posizione della camera nel mondo ----
    private int cameraX = 0;
    private int cameraY = 0;

    // ---- Buffer off-screen per il doppio buffering ----
    private BufferedImage buffer;

    public ArenaPanel() {
        setBackground(BG_DARK);
        setDoubleBuffered(true);

        // Carico le immagini tramite BufferedImageLoader.
        // loadImage() gestisce gia' le eccezioni internamente e torna null se fallisce.
        final BufferedImageLoader loader = new BufferedImageLoader();

        backgroundImage = loader.loadImage("/background/level2.png");
        if (backgroundImage == null) {
            System.err.println("[ArenaPanel] Sfondo non trovato, uso colore di default.");
        }

        basicVirusSprite = loader.loadImage("/sprite/virus/sprite-virus.png");
        if (basicVirusSprite == null) {
            System.err.println("[ArenaPanel] Sprite virus non trovato, uso disegno procedurale.");
        }

        bacteriaSprite = loader.loadImage("/sprite/virus/bacteria-sprite.png");
        if (bacteriaSprite == null) {
            System.err.println("[ArenaPanel] Sprite batterio non trovato, uso disegno procedurale.");
        }
    }

    /**
     * Riceve il nuovo snapshot dal Controller e aggiorna la camera.
     * Chiamato ogni frame dal game loop (tramite SwingUtilities.invokeLater).
     */
    public void updateSnapshot(final GameSnapshot snap) {
        this.snapshot = snap;
        if (snap != null) {
            // Centro la camera sul giocatore, con clamp ai bordi del mondo
            cameraX = clamp((int) snap.playerX - getWidth()  / 2, 0, WORLD_W - getWidth());
            cameraY = clamp((int) snap.playerY - getHeight() / 2, 0, WORLD_H - getHeight());
        }
        repaint();
    }

    // ------------------------------------------------------------------ //
    //  Rendering principale
    // ------------------------------------------------------------------ //

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (snapshot == null) return;

        // Rendo il buffer della stessa dimensione del pannello
        ensureBuffer();
        final Graphics2D g2 = buffer.createGraphics();

        // Anti-aliasing per disegni piu' morbidi
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Sposto il sistema di coordinate in base alla camera
        g2.translate(-cameraX, -cameraY);

        drawBackground(g2);
        drawBorder(g2);
        drawXpOrbs(g2);
        drawProjectiles(g2);
        drawEnemies(g2);
        drawPlayer(g2);

        g2.dispose();
        // Copio il buffer sullo schermo reale
        g.drawImage(buffer, 0, 0, null);
    }

    // ---- Sfondo -------------------------------------------------------

    private void drawBackground(final Graphics2D g2) {
        if (backgroundImage != null) {
            // Scalo il PNG a tutta la dimensione del mondo
            g2.drawImage(backgroundImage, 0, 0, WORLD_W, WORLD_H, null);
        } else {
            // Fallback: sfondo nero se il PNG non e' stato trovato
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, WORLD_W, WORLD_H);
        }
    }

    // ---- Bordo invalicabile dell'arena --------------------------------

    private void drawBorder(final Graphics2D g2) {
        final int thickness = 16;
        // Alone luminoso esterno
        g2.setColor(BORDER_GLOW);
        g2.setStroke(new BasicStroke(thickness + 8));
        g2.drawRect(0, 0, WORLD_W, WORLD_H);
        // Bordo solido
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRect(0, 0, WORLD_W, WORLD_H);
        // Cerchi agli angoli
        final int r = 14;
        final int[][] corners = {{0, 0}, {WORLD_W, 0}, {0, WORLD_H}, {WORLD_W, WORLD_H}};
        for (final int[] c : corners) {
            g2.setColor(BORDER_COLOR);
            g2.fillOval(c[0] - r, c[1] - r, r * 2, r * 2);
        }
    }

    // ---- Orb XP (cristalli viola) -------------------------------------

    private void drawXpOrbs(final Graphics2D g2) {
        if (snapshot.xpOrbs == null) return;
        for (final float[] orb : snapshot.xpOrbs) {
            final float ox = orb[0], oy = orb[1];
            final int r = 6;
            // Alone esterno semitrasparente
            g2.setColor(new Color(162, 155, 254, 80)); // 0xa29bfe con alpha 80
            g2.fillOval((int)(ox - r - 3), (int)(oy - r - 3), (r + 3) * 2, (r + 3) * 2);
            // Nucleo solido
            g2.setColor(XP_ORB_COLOR);
            g2.fillOval((int)(ox - r), (int)(oy - r), r * 2, r * 2);
        }
    }

    // ---- Proiettili ---------------------------------------------------

    private void drawProjectiles(final Graphics2D g2) {
        if (snapshot.projectiles == null) return;
        for (final ProjectileData p : snapshot.projectiles) {
            // Alone giallo semitrasparente
            g2.setColor(new Color(253, 203, 110, 60)); // 0xfdcb6e con alpha 60
            g2.fillOval((int)(p.x - 8), (int)(p.y - 8), 16, 16);
            g2.setColor(PROJ_COLOR);
            if (p.isSpecial) {
                drawStar4(g2, p.x, p.y, 7, p.rotation); // proiettile speciale = stella
            } else {
                g2.fillOval((int)(p.x - 4), (int)(p.y - 4), 8, 8); // proiettile normale
            }
        }
    }

    // ---- Nemici -------------------------------------------------------

    private void drawEnemies(final Graphics2D g2) {
        if (snapshot.enemies == null) return;
        for (final EnemyData e : snapshot.enemies) {
            switch (e.type) {
                case BASIC -> drawBasicVirus(g2, e);
                case FAST  -> drawFastVirus(g2, e);
                case ELITE -> drawEliteVirus(g2, e);
            }
        }
    }

    /** Virus base: usa il PNG se disponibile, altrimenti un cerchio rosso. */
    private void drawBasicVirus(final Graphics2D g2, final EnemyData e) {
        final int r = 14;
        if (basicVirusSprite != null) {
            g2.drawImage(basicVirusSprite, (int) e.x - r, (int) e.y - r, r * 2, r * 2, null);
        } else {
            // Fallback procedurale se il PNG manca
            g2.setColor(new Color(0xe17055));
            g2.fillOval((int)(e.x - r), (int)(e.y - r), r * 2, r * 2);
        }
        drawHealthBar(g2, e);
    }

    /** Virus veloce (batterio): usa il PNG se disponibile, altrimenti ovale procedurale. */
    private void drawFastVirus(final Graphics2D g2, final EnemyData e) {
        final int r = 16;
        if (bacteriaSprite != null) {
            // Ruoto il PNG nella direzione di movimento del batterio
            final AffineTransform old = g2.getTransform();
            g2.translate(e.x, e.y);
            g2.rotate(e.angle);
            g2.drawImage(bacteriaSprite, -r, -r, r * 2, r * 2, null);
            g2.setTransform(old);
        } else {
            // Fallback procedurale: ovale rosa con flagelli
            final AffineTransform old = g2.getTransform();
            g2.translate(e.x, e.y);
            g2.rotate(e.angle);
            g2.setColor(new Color(0xe84393));
            g2.fillOval(-18, -8, 36, 16);
            g2.setColor(new Color(0xfd79a8));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(-18, 0, -26, 0);
            g2.drawLine(-18, 0, -23, -5);
            g2.drawLine(-18, 0, -23,  5);
            g2.setTransform(old);
        }
        drawHealthBar(g2, e);
    }

    /** Virus elite: esagono arancione con alone pulsante. */
    private void drawEliteVirus(final Graphics2D g2, final EnemyData e) {
        final int r = 24;
        // Alone pulsante (l'intensita' varia nel tempo)
        final float pulse = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 300.0);
        g2.setColor(new Color(1f, 0.4f, 0f, 0.15f + 0.1f * pulse));
        g2.fillOval((int)(e.x - r - 12), (int)(e.y - r - 12), (r + 12) * 2, (r + 12) * 2);
        // Corpo esagonale
        final int[] xs = new int[6], ys = new int[6];
        for (int i = 0; i < 6; i++) {
            final double a = Math.PI / 3.0 * i - Math.PI / 6.0;
            xs[i] = (int) e.x + (int)(Math.cos(a) * r);
            ys[i] = (int) e.y + (int)(Math.sin(a) * r);
        }
        g2.setColor(new Color(0xe17055));
        g2.fill(new Polygon(xs, ys, 6));
        drawHealthBar(g2, e);
    }

    // ---- Personaggio principale (la cellula) --------------------------

    private void drawPlayer(final Graphics2D g2) {
        final int px = (int) snapshot.playerX;
        final int py = (int) snapshot.playerY;
        final int r  = 18;

        // Alone esterno semitrasparente
        g2.setColor(new Color(85, 239, 196, 30)); // 0x55efc4 alpha 30
        g2.fillOval(px - r - 10, py - r - 10, (r + 10) * 2, (r + 10) * 2);

        // Membrana cellulare
        g2.setColor(PLAYER_COLOR);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(px - r, py - r, r * 2, r * 2);
        g2.setColor(new Color(85, 239, 196, 40)); // riempimento semitrasparente
        g2.fillOval(px - r, py - r, r * 2, r * 2);

        // Nucleo cellulare (cerchio centrale giallo)
        final int nr = 7;
        g2.setColor(PLAYER_NUCLEUS);
        g2.fillOval(px - nr, py - nr, nr * 2, nr * 2);

        // Organelli secondari (piccoli cerchi attorno al nucleo)
        g2.setColor(new Color(0, 184, 148, 160)); // 0x00b894 alpha 160
        final int[] oX = {px - 8, px + 7, px - 4};
        final int[] oY = {py + 5, py - 6, py - 9};
        for (int i = 0; i < oX.length; i++) {
            g2.fillOval(oX[i] - 3, oY[i] - 3, 6, 6);
        }

        // Flash di invincibilita' (lampeggia dopo aver subito danno)
        if (snapshot.isInvincible) {
            final float alpha = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 80.0);
            g2.setColor(new Color(1f, 1f, 1f, alpha));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(px - r - 2, py - r - 2, (r + 2) * 2, (r + 2) * 2);
        }
    }

    // ---- Barra HP sopra ogni nemico ----------------------------------

    private void drawHealthBar(final Graphics2D g2, final EnemyData e) {
        final int bw = 30, bh = 4;
        final int bx = (int)(e.x - bw / 2.0);
        final int by = (int)(e.y - 22);

        // Sfondo grigio scuro
        g2.setColor(new Color(0x2d3436));
        g2.fillRect(bx, by, bw, bh);

        // Calcolo ratio HP (0 = morto, 1 = pieno)
        final float ratio = Math.max(0, Math.min(1, (float) e.hp / e.maxHp));

        // Interpolazione colore: rosso (0xff7675) quando hp e' basso,
        // verde (0x55efc4) quando hp e' pieno
        final int red   = (int)(0xff + (0x55 - 0xff) * ratio);
        final int green = (int)(0x76 + (0xef - 0x76) * ratio);
        final int blue  = (int)(0x75 + (0xc4 - 0x75) * ratio);
        g2.setColor(new Color(red, green, blue));
        g2.fillRect(bx, by, (int)(bw * ratio), bh);
    }

    // ================================================================== //
    //  Utility geometriche
    // ================================================================== //

    /** Disegna una stella a 4 punte (proiettile speciale). */
    private void drawStar4(final Graphics2D g2,
                           final float cx, final float cy,
                           final float r, final float rot) {
        final int[] xs = new int[8], ys = new int[8];
        for (int i = 0; i < 8; i++) {
            final double a = Math.PI / 4.0 * i + rot;
            final float radius = (i % 2 == 0) ? r : r * 0.4f;
            xs[i] = (int)(cx + Math.cos(a) * radius);
            ys[i] = (int)(cy + Math.sin(a) * radius);
        }
        g2.fill(new Polygon(xs, ys, 8));
    }

    /** Clamp di un valore tra min e max. */
    private int clamp(final int v, final int min, final int max) {
        return Math.max(min, Math.min(max, v));
    }

    /** Ricrea il buffer se le dimensioni del pannello sono cambiate. */
    private void ensureBuffer() {
        if (buffer == null
                || buffer.getWidth()  != getWidth()
                || buffer.getHeight() != getHeight()) {
            buffer = new BufferedImage(
                    Math.max(1, getWidth()),
                    Math.max(1, getHeight()),
                    BufferedImage.TYPE_INT_ARGB);
        }
    }
}
