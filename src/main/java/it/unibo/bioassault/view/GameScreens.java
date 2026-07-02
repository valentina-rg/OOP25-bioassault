package it.unibo.bioassault.view;

import it.unibo.bioassault.model.UpgradeOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Raccoglie tutte le schermate overlay del gioco come inner class statiche:
 *
 *   - MainMenuScreen  → menu principale con animazione sfondo
 *   - PauseScreen     → overlay di pausa con bottoni Riprendi / Menu
 *   - LevelUpScreen   → scelta tra 3 upgrade al salire di livello
 *   - GameOverScreen  → statistiche finali con opzione Rigioca / Menu
 *
 * Ogni schermata e' un JPanel autonomo.
 * Si aggiungono/rimuovono al JLayeredPane di GameWindow tramite
 * showOverlay() e removeCurrentOverlay().
 */
public class GameScreens {

    // ================================================================== //
    //  MENU PRINCIPALE
    // ================================================================== //

    public static class MainMenuScreen extends JPanel {

        private final Runnable onStart;
        private final Runnable onQuit;
        private final long     startTime = System.currentTimeMillis();
        private final Timer    animTimer;

        public MainMenuScreen(final Runnable onStart, final Runnable onQuit) {
            this.onStart = onStart;
            this.onQuit  = onQuit;
            setOpaque(true);
            setBackground(new Color(10, 26, 14)); // 0x0a1a0e
            setLayout(null);

            // Bottoni principali
            final JButton btnStart = styledButton("INIZIA", new Color(0x2ecc71), Color.BLACK);
            final JButton btnQuit  = styledButton("ESCI",   new Color(0x636e72), Color.WHITE);
            btnStart.addActionListener(e -> onStart.run());
            btnQuit .addActionListener(e -> onQuit.run());
            add(btnStart);
            add(btnQuit);

            // Ricalcolo posizioni quando la finestra viene ridimensionata
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    final int cx = getWidth() / 2 - 100;
                    final int cy = getHeight() / 2 + 60;
                    btnStart.setBounds(cx, cy,       200, 48);
                    btnQuit .setBounds(cx, cy + 64,  200, 48);
                }
            });

            // Timer che aggiorna l'animazione di sfondo a ~60fps
            animTimer = new Timer(16, e -> repaint());
            animTimer.start();
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final int  W = getWidth(), H = getHeight();
            final long t = System.currentTimeMillis() - startTime;

            // Sfondo: virus che galleggiano lentamente
            drawFloatingViruses(g2, W, H, t);

            // Titolo: "BIO ASSAULT" in due righe
            final Font titleFont = new Font("Monospaced", Font.BOLD, 52);
            g2.setFont(titleFont);
            final FontMetrics fm = g2.getFontMetrics(titleFont);
            final String title1 = "BIO";
            final String title2 = "ASSAULT";

            // Il verde pulsa leggermente nel tempo
            final float pulse = 0.85f + 0.15f * (float) Math.sin(t / 800.0);
            g2.setColor(new Color(0.21f * pulse, 0.93f * pulse, 0.44f * pulse, 1f));
            g2.drawString(title1, (W - fm.stringWidth(title1)) / 2, H / 2 - 80);

            g2.setColor(new Color(0xdfe6e9));
            g2.drawString(title2, (W - fm.stringWidth(title2)) / 2, H / 2 - 22);

            // Sottotitolo fisso
            g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
            g2.setColor(new Color(0x636e72));
            final String sub = "La cellula deve sopravvivere all'invasione dei virus";
            final FontMetrics fmS = g2.getFontMetrics();
            g2.drawString(sub, (W - fmS.stringWidth(sub)) / 2, H / 2 + 24);

            g2.dispose();
        }

        /** Disegna virus semitrasparenti che si muovono lentamente sullo sfondo. */
        private void drawFloatingViruses(final Graphics2D g2,
                                         final int W, final int H, final long t) {
            final int count = 18;
            for (int i = 0; i < count; i++) {
                final double phase = (double) i / count;
                final float x = (float)((Math.sin(phase * 12.7 + t / 4000.0) * 0.4 + 0.5) * W);
                final float y = (float)((Math.cos(phase * 9.3  + t / 5500.0) * 0.4 + 0.5) * H);
                final float r = 6 + (float)(Math.sin(phase * 7) * 4);
                final float alpha = 0.08f + 0.06f * (float) Math.sin(phase * 5 + t / 2000.0);
                g2.setColor(new Color(0.18f, 0.8f, 0.44f, alpha));
                g2.fillOval((int)(x - r), (int)(y - r), (int)(r * 2), (int)(r * 2));
            }
        }

        /** Da chiamare quando si esce dal menu per fermare il timer d'animazione. */
        public void dispose() {
            animTimer.stop();
        }
    }

    // ================================================================== //
    //  SCHERMATA DI PAUSA
    // ================================================================== //

    public static class PauseScreen extends JPanel {

        public PauseScreen(final Runnable onResume, final Runnable onQuitToMenu) {
            setOpaque(false); // trasparente: si vede l'arena in pausa sotto
            setLayout(null);

            final JButton btnResume = styledButton("RIPRENDI", new Color(0x2ecc71), Color.BLACK);
            final JButton btnMenu   = styledButton("MENU",     new Color(0x636e72), Color.WHITE);
            btnResume.addActionListener(e -> onResume.run());
            btnMenu  .addActionListener(e -> onQuitToMenu.run());
            add(btnResume);
            add(btnMenu);

            // Ricalcola posizioni al resize
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    final int cx = getWidth() / 2 - 100;
                    final int cy = getHeight() / 2 + 10;
                    btnResume.setBounds(cx, cy,       200, 44);
                    btnMenu  .setBounds(cx, cy + 56,  200, 44);
                }
            });
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g.create();

            // Overlay scuro sull'intera finestra
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Box centrale con bordo verde
            final int bw = 340, bh = 250;
            final int bx = (getWidth() - bw) / 2;
            final int by = (getHeight() - bh) / 2;
            g2.setColor(new Color(15, 34, 18, 230));   // 0x0f2212 alpha 230
            g2.fillRoundRect(bx, by, bw, bh, 16, 16);
            g2.setColor(new Color(46, 204, 113, 120));  // 0x2ecc71 alpha 120
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(bx, by, bw, bh, 16, 16);

            // Titolo
            g2.setFont(new Font("Monospaced", Font.BOLD, 28));
            g2.setColor(new Color(0xdfe6e9));
            g2.drawString("PAUSA", (getWidth() - 90) / 2, by + 52);

            g2.dispose();
        }
    }

    // ================================================================== //
    //  SCELTA UPGRADE AL LEVEL-UP
    // ================================================================== //

    public static class LevelUpScreen extends JPanel {

        private final List<UpgradeOption> options;
        private final IntConsumer         onChoice; // riceve 0, 1 o 2 (indice della card)
        private int hovered = -1;

        public LevelUpScreen(final List<UpgradeOption> options, final IntConsumer onChoice) {
            this.options  = options;
            this.onChoice = onChoice;
            setOpaque(false);
            setLayout(null);

            // Hover con il mouse -> evidenzia la card
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(final MouseEvent e) {
                    final int prev = hovered;
                    hovered = cardIndexAt(e.getX(), e.getY());
                    if (hovered != prev) repaint();
                }
            });

            // Click su una card -> chiama il callback con l'indice scelto
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    final int idx = cardIndexAt(e.getX(), e.getY());
                    if (idx >= 0) onChoice.accept(idx);
                }
            });
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            if (options == null || options.isEmpty()) return;

            final Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final int W = getWidth(), H = getHeight();

            // Overlay scuro
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, W, H);

            // Titolo
            g2.setFont(new Font("Monospaced", Font.BOLD, 26));
            g2.setColor(new Color(0xa29bfe));
            final String title = "LIVELLO SUPERIORE! SCEGLI UN POTENZIAMENTO";
            final FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (W - fm.stringWidth(title)) / 2, H / 2 - 130);

            // Hint tasti
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g2.setColor(new Color(0x636e72));
            g2.drawString("Tasti 1, 2, 3 oppure click", (W - 190) / 2, H / 2 - 108);

            // Le 3 card degli upgrade
            final int cardW = 200, cardH = 180, gap = 24;
            final int totalW = 3 * cardW + 2 * gap;
            final int startX = (W - totalW) / 2;
            final int cardY  = H / 2 - 80;

            for (int i = 0; i < Math.min(3, options.size()); i++) {
                drawUpgradeCard(g2, options.get(i),
                        startX + i * (cardW + gap), cardY,
                        cardW, cardH, i == hovered, i);
            }

            g2.dispose();
        }

        /** Disegna una singola card di upgrade. */
        private void drawUpgradeCard(final Graphics2D g2, final UpgradeOption opt,
                                     final int x, final int y,
                                     final int w, final int h,
                                     final boolean isHovered, final int index) {
            // Sfondo e bordo cambiano se la card e' in hover
            g2.setColor(isHovered ? new Color(0x1a3320) : new Color(0x0f2212));
            g2.fillRoundRect(x, y, w, h, 12, 12);
            g2.setColor(isHovered ? new Color(0xa29bfe) : new Color(0x2d3436));
            g2.setStroke(new BasicStroke(isHovered ? 2f : 1f));
            g2.drawRoundRect(x, y, w, h, 12, 12);

            // Numero tasto [1], [2], [3]
            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            g2.setColor(new Color(0x636e72));
            g2.drawString("[" + (index + 1) + "]", x + 8, y + 18);

            // Icona testuale (es. "★", "⚡", "❤")
            g2.setFont(new Font("Serif", Font.PLAIN, 36));
            g2.setColor(new Color(0xdfe6e9));
            FontMetrics fm = g2.getFontMetrics();
            final String icon = opt.icon != null ? opt.icon : "★";
            g2.drawString(icon, x + (w - fm.stringWidth(icon)) / 2, y + 66);

            // Nome dell'upgrade
            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.setColor(isHovered ? new Color(0xa29bfe) : new Color(0xdfe6e9));
            fm = g2.getFontMetrics();
            g2.drawString(opt.name, x + (w - fm.stringWidth(opt.name)) / 2, y + 94);

            // Descrizione con wrap automatico
            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2.setColor(new Color(0xb2bec3));
            drawWrappedText(g2, opt.description, x + 10, y + 112, w - 20, 14);
        }

        /** Restituisce l'indice (0-2) della card sotto il cursore, o -1 se fuori. */
        private int cardIndexAt(final int mx, final int my) {
            final int W = getWidth(), H = getHeight();
            final int cardW = 200, cardH = 180, gap = 24;
            final int startX = (W - (3 * cardW + 2 * gap)) / 2;
            final int cardY  = H / 2 - 80;
            for (int i = 0; i < 3; i++) {
                final int cx = startX + i * (cardW + gap);
                if (mx >= cx && mx <= cx + cardW && my >= cardY && my <= cardY + cardH) {
                    return i;
                }
            }
            return -1;
        }

        /** Disegna una stringa andando a capo automaticamente se supera maxW. */
        private void drawWrappedText(final Graphics2D g2, final String text,
                                     final int x, final int y,
                                     final int maxW, final int lineH) {
            final FontMetrics fm = g2.getFontMetrics();
            final String[] words = text.split(" ");
            final StringBuilder line = new StringBuilder();
            int curY = y;
            for (final String w : words) {
                final String test = line.isEmpty() ? w : line + " " + w;
                if (fm.stringWidth(test) > maxW) {
                    g2.drawString(line.toString(), x, curY);
                    line.setLength(0);
                    line.append(w);
                    curY += lineH;
                } else {
                    if (!line.isEmpty()) line.append(" ");
                    line.append(w);
                }
            }
            if (!line.isEmpty()) g2.drawString(line.toString(), x, curY);
        }
    }

    // ================================================================== //
    //  GAME OVER / STATISTICHE FINALI
    // ================================================================== //

    public static class GameOverScreen extends JPanel {

        /**
         * Statistiche di fine partita mostrate nella schermata.
         * Uso un record (Java 16+) perche' e' immutabile e compatto.
         */
        public record Stats(
            int survivalSeconds,
            int wave,
            int level,
            int enemiesKilled,
            int damageDealt,
            boolean victory
        ) {}

        // Le statistiche da mostrare, salvate al momento della creazione
        private final Stats stats;

        public GameOverScreen(final Stats stats,
                              final Runnable onRestart,
                              final Runnable onMenu) {
            this.stats = stats;
            setOpaque(false);
            setLayout(null);

            final JButton btnRestart = styledButton("RIGIOCA",         new Color(0x2ecc71), Color.BLACK);
            final JButton btnMenu    = styledButton("MENU PRINCIPALE",  new Color(0x636e72), Color.WHITE);
            btnRestart.addActionListener(e -> onRestart.run());
            btnMenu   .addActionListener(e -> onMenu.run());
            add(btnRestart);
            add(btnMenu);

            // Posiziona i bottoni al centro-basso del pannello
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    final int cx = getWidth() / 2 - 100;
                    final int cy = getHeight() / 2 + 130;
                    btnRestart.setBounds(cx,       cy,      200, 44);
                    btnMenu   .setBounds(cx, cy + 56, 200, 44);
                }
            });

            // Piccola animazione per mantenere il pannello "vivo"
            new Timer(16, e -> repaint()).start();
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            if (stats == null) return;

            final Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final int W = getWidth(), H = getHeight();

            // Overlay scuro
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, W, H);

            // Box centrale
            final int bw = 420, bh = 380;
            final int bx = (W - bw) / 2, by = (H - bh) / 2;
            g2.setColor(new Color(10, 26, 14, 240)); // 0x0a1a0e alpha 240
            g2.fillRoundRect(bx, by, bw, bh, 16, 16);

            // Bordo verde se vittoria, rosso se sconfitta
            final Color borderCol = stats.victory()
                    ? new Color(0x2ecc71) : new Color(0xff7675);
            g2.setColor(borderCol);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(bx, by, bw, bh, 16, 16);

            // Titolo
            final String titleStr = stats.victory()
                    ? "CELLULA SOPRAVVISSUTA!" : "CELLULA ELIMINATA";
            g2.setFont(new Font("Monospaced", Font.BOLD, 24));
            g2.setColor(borderCol);
            final FontMetrics fm = g2.getFontMetrics();
            g2.drawString(titleStr, bx + (bw - fm.stringWidth(titleStr)) / 2, by + 50);

            // Tabella statistiche
            final String[] labels = {
                "Tempo sopravvissuto",
                "Ondata raggiunta",
                "Livello finale",
                "Nemici eliminati",
                "Danno totale inflitto"
            };
            final String[] values = {
                formatTime(stats.survivalSeconds()),
                "W"   + stats.wave(),
                "LV " + stats.level(),
                String.valueOf(stats.enemiesKilled()),
                String.valueOf(stats.damageDealt())
            };

            g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
            final int rowH = 34, startY = by + 90;
            for (int i = 0; i < labels.length; i++) {
                final int rowY = startY + i * rowH;
                // Riga alternata leggermente piu' chiara
                if (i % 2 == 0) {
                    g2.setColor(new Color(255, 255, 255, 8));
                    g2.fillRect(bx + 10, rowY - 18, bw - 20, rowH - 2);
                }
                g2.setColor(new Color(0x636e72));
                g2.drawString(labels[i], bx + 24, rowY);
                g2.setColor(new Color(0xdfe6e9));
                final FontMetrics fmV = g2.getFontMetrics();
                g2.drawString(values[i], bx + bw - 24 - fmV.stringWidth(values[i]), rowY);
            }

            // Separatore
            g2.setColor(new Color(0x2d3436));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(bx + 20, by + 270, bx + bw - 20, by + 270);

            g2.dispose();
        }

        /** Formatta i secondi in mm:ss */
        private String formatTime(final int totalSeconds) {
            return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
        }
    }

    // ================================================================== //
    //  Helper condiviso: bottone stilizzato
    // ================================================================== //

    /**
     * Crea un bottone con sfondo arrotondato e hover interattivo.
     * Usato da tutte le schermate per avere uno stile uniforme.
     */
    private static JButton styledButton(final String text,
                                        final Color bg, final Color fg) {
        final JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(final Graphics g) {
                final Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Cambia tonalita' al click e all'hover
                final Color c = getModel().isPressed()  ? bg.darker().darker()
                              : getModel().isRollover() ? bg.brighter()
                              : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(fg);
                g2.setFont(new Font("Monospaced", Font.BOLD, 14));
                final FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
