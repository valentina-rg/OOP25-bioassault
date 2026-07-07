package it.unibo.bioassault.view;
import it.unibo.bioassault.model.GameSnapshot;
import javax.swing.*;
import java.awt.*;
/**
 * HUD trasparente sovrapposto all'arena.
 * Disegna:
 *   - Barra HP del giocatore (in alto a sinistra)
 *   - Livello e barra XP
 *   - Timer di sopravvivenza (al centro)
 *   - Numero ondata e nemici a schermo (in alto a destra)
 *   - Slot armi (in basso a sinistra)
 *   - Minimappa (in basso a destra)
 *   - Annuncio ondata con fade in/out (al centro schermo, comparsa temporanea)
 */
public class HudPanel extends JPanel {

    // ---- Colori HUD ----
    private static final Color HUD_BG      = new Color(0,   0,   0,   100); // sfondo barre semitrasparente
    private static final Color HP_HIGH     = new Color(0x55efc4); // verde (hp alto)
    private static final Color HP_MED      = new Color(0xfdcb6e); // giallo (hp medio)
    private static final Color HP_LOW      = new Color(0xff7675); // rosso (hp basso)
    private static final Color XP_COLOR    = new Color(0xa29bfe); // viola
    private static final Color TEXT_COLOR  = new Color(0xdfe6e9); // bianco tenue
    private static final Color TEXT_DIMMED = new Color(0x636e72); // grigio
    private static final Color WAVE_COLOR  = new Color(0x2ecc71); // verde brillante

    // ---- Font riutilizzati ----
    private static final Font FONT_MAIN  = new Font("Monospaced", Font.BOLD,  13);
    private static final Font FONT_LARGE = new Font("Monospaced", Font.BOLD,  22);
    private static final Font FONT_SMALL = new Font("Monospaced", Font.PLAIN, 11);

    // ---- Dimensioni HUD ----
    private static final int TOP_BAR_HEIGHT    = 48;
    private static final int BOTTOM_BAR_HEIGHT = 36;
    private static final int MINIMAP_WIDTH     = 100;
    private static final int MINIMAP_HEIGHT    = 75;
    private static final int MINIMAP_MARGIN    = 10;

    // ---- Snapshot corrente ----
    private GameSnapshot snapshot = null;

    // ---- Per l'animazione dell'annuncio ondata ----
    private int  lastWave     = 0;
    private long waveShowTime = 0;
    // Durata in ms durante cui l'annuncio ondata rimane visibile
    private static final long WAVE_DISPLAY_MS = 3000;

    public HudPanel() {
        // Trasparente: si vede l'arena sotto
        setOpaque(false);
    }

    /*
     * Riceve il nuovo snapshot e aggiorna l'HUD.
     * Se l'ondata e' cambiata, avvia l'animazione di annuncio.
     */
    public void updateSnapshot(final GameSnapshot snap) {
        if (snap != null && snap.wave != lastWave) {
            lastWave     = snap.wave;
            waveShowTime = System.currentTimeMillis();
        }
        this.snapshot = snap;
        repaint();
    }


    //  Rendering
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (snapshot == null) {
            return;
        }

        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawTopBar(g2);
        drawBottomBar(g2);
        drawWaveAnnouncement(g2);
        drawMinimap(g2);

        g2.dispose();
    }

    // ---- Barra superiore: HP, livello, XP, timer, ondata ----

    private void drawTopBar(final Graphics2D g2) {
        final int width = getWidth();
        final int barH = TOP_BAR_HEIGHT;
        g2.setColor(HUD_BG);
        g2.fillRect(0, 0, width, barH);
        final int padding = 14;

        // HP
        drawLabel(g2, "HP", padding, 10, TEXT_DIMMED, FONT_SMALL);
        final float hpRatio = (float) snapshot.playerHp / snapshot.playerMaxHp;
        // Colore cambia in base alla percentuale di vita rimasta
        final Color hpColor = hpRatio > 0.5f ? HP_HIGH : hpRatio > 0.25f ? HP_MED : HP_LOW;
        drawBar(g2, padding, 20, 140, 12, hpRatio, hpColor, new Color(0x2d3436));
        drawLabel(g2, snapshot.playerHp + "/" + snapshot.playerMaxHp,
                padding + 5, 32, hpColor, FONT_SMALL);

        //vLivello
        final int lvX = padding + 160;
        drawLabel(g2, "LV",  lvX,      10, TEXT_DIMMED, FONT_SMALL);
        drawLabel(g2, String.valueOf(snapshot.level), lvX + 20, 34, TEXT_COLOR, FONT_LARGE);

        // XP 
        final int xpX = lvX + 60;
        drawLabel(g2, "XP", xpX, 10, TEXT_DIMMED, FONT_SMALL);
        final float xpRatio = snapshot.xpToNext > 0
                ? (float) snapshot.xp / snapshot.xpToNext : 1f;
        drawBar(g2, xpX, 20, 140, 12, xpRatio, XP_COLOR, new Color(0x2d3436));
        drawLabel(g2, snapshot.xp + "/" + snapshot.xpToNext,
                xpX + 5, 32, XP_COLOR, FONT_SMALL);

        // Timer di sopravvivenza (centrato)
        final String time = formatTime(snapshot.survivalSeconds);
        final FontMetrics fm = g2.getFontMetrics(FONT_LARGE);
        drawLabel(g2, time, (width - fm.stringWidth(time)) / 2, 34, TEXT_COLOR, FONT_LARGE);
        drawLabel(g2, "SOPRAVVIVENZA", (width - 120) / 2, 12, TEXT_DIMMED, FONT_SMALL);

        // Ondata (in alto a destra) 
        final int wX = width - 160;
        drawLabel(g2, "ONDATA",                           wX,      10, TEXT_DIMMED, FONT_SMALL);
        drawLabel(g2, "W" + snapshot.wave,                wX,      34, WAVE_COLOR,  FONT_LARGE);
        drawLabel(g2, snapshot.enemiesOnScreen + " nemici", wX + 48, 26, TEXT_DIMMED, FONT_SMALL);
    }

    // Barra inferiore: slot armi e tasti 

    private void drawBottomBar(final Graphics2D g2) {
        final int width = getWidth();
        final int height = getHeight();
        final int barH = BOTTOM_BAR_HEIGHT;
        g2.setColor(HUD_BG);
        g2.fillRect(0, height - barH, width, barH);

        // Slot armi (massimo 6 slot)
        final int slotSize = 28, slotY = height - barH + 4, spacing = 36;
        for (int i = 0; i < snapshot.weapons.length; i++) {
            final int slotX  = 10 + i * spacing;
            final boolean active = snapshot.weapons[i] != null;
            // Sfondo slot
            g2.setColor(active ? new Color(0x2d3436) : new Color(0x1a1a1a));
            g2.fillRoundRect(slotX, slotY, slotSize, slotSize, 6, 6);
            g2.setColor(active ? new Color(0x636e72) : new Color(0x333333));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(slotX, slotY, slotSize, slotSize, 6, 6);
            // Icona testuale (prime 2 lettere del nome arma)
            if (active) {
                g2.setColor(TEXT_COLOR);
                g2.setFont(FONT_SMALL);
                g2.drawString(snapshot.weapons[i].substring(0, Math.min(2, snapshot.weapons[i].length())),
                        slotX + 7, slotY + 19);
            }
        }

        // Hint tasti al centro della barra inferiore
        drawLabel(g2, "WASD / FRECCE = muovi   ESC = pausa",
                width / 2 - 130, height - barH + 22, TEXT_DIMMED, FONT_SMALL);
    }

    // Annuncio ondata 
    private void drawWaveAnnouncement(final Graphics2D g2) {
        final long elapsed = System.currentTimeMillis() - waveShowTime;
        if (elapsed > WAVE_DISPLAY_MS) {
            return;
        }

        final int width = getWidth();
        final int height = getHeight();
        final float progress = (float) elapsed / WAVE_DISPLAY_MS;

        // Calcolo alpha per fade-in (0-20%) e fade-out (70-100%)
        final float alpha = progress < 0.2f
                ? progress / 0.2f
                : progress > 0.7f ? 1f - (progress - 0.7f) / 0.3f
                : 1f;

        // Linee orizzontali
        final int lY = height / 2 - 30;
        g2.setColor(new Color(WAVE_COLOR.getRed(), WAVE_COLOR.getGreen(),
                WAVE_COLOR.getBlue(), (int)(alpha * 180)));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(width / 2 - 200, lY,      width / 2 + 200, lY);
        g2.drawLine(width / 2 - 200, lY + 60, width / 2 + 200, lY + 60);

        // Testo "ONDATA X"
        final Font waveFontBig   = new Font("Monospaced", Font.BOLD,  32);
        final Font waveFontSmall = new Font("Monospaced", Font.PLAIN, 14);
        final String waveStr = "ONDATA " + lastWave;
        g2.setFont(waveFontBig);
        g2.setColor(new Color(WAVE_COLOR.getRed(), WAVE_COLOR.getGreen(),
                WAVE_COLOR.getBlue(), (int)(alpha * 255)));
        final FontMetrics fm = g2.getFontMetrics(waveFontBig);
        g2.drawString(waveStr, (width - fm.stringWidth(waveStr)) / 2, height / 2);

        // Sottotitolo
        final String sub = lastWave == 1 ? "LA CELLULA COMBATTE!" : "I VIRUS SI MOLTIPLICANO...";
        g2.setFont(waveFontSmall);
        g2.setColor(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(),
                TEXT_COLOR.getBlue(), (int)(alpha * 180)));
        final FontMetrics fmS = g2.getFontMetrics(waveFontSmall);
        g2.drawString(sub, (width - fmS.stringWidth(sub)) / 2, height / 2 + 22);
    }

    // Minimappa (angolo in basso a destra) 
    private void drawMinimap(final Graphics2D g2) {
        final int width = getWidth();
        final int height = getHeight();
        final int mmW = MINIMAP_WIDTH;
        final int mmH = MINIMAP_HEIGHT;
        final int margin = MINIMAP_MARGIN;
        final int mmX = width - mmW - margin;
        final int mmY = height - mmH - 40;

        // Sfondo minimappa
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(mmX, mmY, mmW, mmH, 6, 6);
        g2.setColor(new Color(46, 204, 113, 100)); // bordo verde semitrasparente
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(mmX, mmY, mmW, mmH, 6, 6);

        // Scala per convertire coordinate mondo -> minimappa
        final float scaleX = (float) mmW / ArenaPanel.WORLD_W;
        final float scaleY = (float) mmH / ArenaPanel.WORLD_H;

        // Nemici (puntini rossi)
        if (snapshot.enemies != null) {
            g2.setColor(new Color(255, 118, 117, 200)); // 0xff7675 alpha 200
            for (final var e : snapshot.enemies) {
                final int ex = mmX + (int)(e.x * scaleX);
                final int ey = mmY + (int)(e.y * scaleY);
                g2.fillRect(ex - 1, ey - 1, 2, 2);
            }
        }

        // Giocatore (puntino verde, piu' grande dei nemici)
        final int px = mmX + (int)(snapshot.playerX * scaleX);
        final int py = mmY + (int)(snapshot.playerY * scaleY);
        g2.setColor(HP_HIGH);
        g2.fillOval(px - 3, py - 3, 6, 6);

        // Etichetta "MAPPA"
        g2.setFont(FONT_SMALL);
        g2.setColor(TEXT_DIMMED);
        g2.drawString("MAPPA", mmX + 2, mmY - 2);
    }

    /** Disegna una barra (HP, XP)*/
    private void drawBar(final Graphics2D g2,
                         final int x, final int y, final int w, final int h,
                         final float ratio, final Color fill, final Color bg) {
        g2.setColor(bg);
        g2.fillRoundRect(x, y, w, h, 4, 4);
        if (ratio > 0) {
            g2.setColor(fill);
            g2.fillRoundRect(x, y, (int)(w * ratio), h, 4, 4);
        }
    }

    /** Disegna una stringa con font e colore specificati. */
    private void drawLabel(final Graphics2D g2, final String text,
                           final int x, final int y,
                           final Color color, final Font font) {
        g2.setFont(font);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    /** Formatta i secondi in mm:ss */
    private String formatTime(final int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}
