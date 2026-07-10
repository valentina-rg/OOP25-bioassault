package it.unibo.bioassault.view;

import it.unibo.bioassault.model.stats.RunStats;
import it.unibo.bioassault.model.stats.Upgrade;

import java.awt.*;
import java.time.Duration;
import java.util.List;

/**
 * Full-screen overlay shown when the game ends (win or loss).
 * Displays all real RunStats values.
 */
public class GameOverScreen {

    private static final Color BG = new Color(10, 10, 30, 220);
    private static final Color TITLE_WIN = new Color(80, 255, 80);
    private static final Color TITLE_LOSE = new Color(255, 80, 80);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color LABEL_COLOR = new Color(150, 180, 255);
    private static final Color SCORE_COLOR = new Color(255, 220, 50);
    private static final Color BORDER_COLOR = new Color(100, 100, 200);

    private final int screenW;
    private final int screenH;

    public GameOverScreen(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
    }

    /**
     * Renders the game-over/victory screen with live statistics.
     *
     * @param g        the graphics context
     * @param runStats 
     * @param level    the final player level
     * @param upgrades the list of upgrades acquired during the run
     * @param survived true for victory screen, false for game-over screen
     */
    public void render(Graphics g, RunStats runStats, int level,
                       List<Upgrade> upgrades, boolean survived) {

        // Background
        g.setColor(BG);
        g.fillRect(0, 0, screenW, screenH);

        // Border box
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(screenW / 2 - 260, 30, 520, screenH - 60, 20, 20);
        g2d.setStroke(new BasicStroke(1));

        int x = screenW / 2 - 220;
        int y = 80;
        final int lineH = 28;

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(survived ? TITLE_WIN : TITLE_LOSE);
        String title = survived ? "✨  VITTORIA!  ✨" : "💀  GAME OVER  💀";
        FontMetrics fmT = g.getFontMetrics();
        g.drawString(title, screenW / 2 - fmT.stringWidth(title) / 2, y);
        y += 50;

        // Stats
        Duration time = runStats.getSurvivalTime();
        String timeStr = String.format("%02d:%02d", time.toMinutes(), time.toSecondsPart());

        drawRow(g, x, y, "Livello raggiunto",  String.valueOf(level));          
        y += lineH;

        drawRow(g, x, y, "Tempo sopravvissuto", timeStr);                       
        y += lineH;
        y += 8;

        drawRow(g, x, y, "Nemici eliminati", String.valueOf(runStats.getKills()));       
        y += lineH;
        drawRow(g, x, y, "  di cui élite", String.valueOf(runStats.getEliteKills()));  
        y += lineH;
        drawRow(g, x, y, "Kill/minuto", String.format("%.1f", runStats.getKillsPerMinute())); 
        y += lineH;
        y += 8;

        drawRow(g, x, y, "XP totale", String.valueOf(runStats.getTotalXp()));     
        y += lineH;
        drawRow(g, x, y, "Danno inflitto", String.format("%.0f", runStats.getDamageDealt())); 
        y += lineH;
        drawRow(g, x, y, "Danno subito", String.format("%.0f", runStats.getDamageTaken())); 
        y += lineH;
        y += 8;

        // Upgrades
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(LABEL_COLOR);
        g.drawString("Upgrade scelti:", x, y);
        y += lineH;
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.setColor(TEXT_COLOR);
        if (upgrades.isEmpty()) {
            g.drawString(" (nessuno)", x, y);
            y += lineH;
        } else {
            for (Upgrade u : upgrades) {
                g.drawString("  • " + u.getName(), x, y);
                y += lineH - 4;
            }
        }
        y += 12;

        // Final score
        long score = runStats.computeFinalScore(level);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.setColor(SCORE_COLOR);
        String scoreStr = "★  PUNTEGGIO FINALE:  " + score;
        FontMetrics fmS = g.getFontMetrics();
        g.drawString(scoreStr, screenW / 2 - fmS.stringWidth(scoreStr) / 2, y + 10);
    }

    private void drawRow(Graphics g, int x, int y, String label, String value) {
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(LABEL_COLOR);
        g.drawString(label + ":", x, y);
        g.setColor(TEXT_COLOR);
        g.drawString(value, x + 240, y);
    }
}
