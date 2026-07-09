package it.unibo.bioassault.view;

import it.unibo.bioassault.model.stats.Upgrade;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * Overlay that pauses the game and lets the player choose one upgrade on level-up.
 */
public class LevelUpScreen {

    private static final int CARD_W = 220;
    private static final int CARD_H = 130;
    private static final int CARD_GAP = 20;
    private static final Color BG_OVERLAY   = new Color(0, 0, 0, 180);
    private static final Color CARD_DEFAULT = new Color(30, 30, 80, 230);
    private static final Color CARD_HOVER   = new Color(60, 60, 160, 240);
    private static final Color CARD_BORDER  = new Color(150, 150, 255);
    private static final Color TITLE_COLOR  = new Color(255, 220, 50);

    private boolean visible = false;
    private List<Upgrade> choices;
    private Consumer<Upgrade> onChoice;
    private int newLevel;
    private int hoveredCard = -1;

    private final int screenW;
    private final int screenH;
    private final MouseAdapter mouseAdapter;

    public LevelUpScreen(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;

        this.mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredCard = cardAt(e.getX(), e.getY());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!visible) return;
                int idx = cardAt(e.getX(), e.getY());
                if (idx >= 0 && idx < choices.size()) {
                    Upgrade chosen = choices.get(idx);
                    hide();
                    if (onChoice != null) onChoice.accept(chosen);
                }
            }
        };
    }

    /** Shows the overlay with the given upgrade choices. Calls onChoice when the player picks. */
    public void show(int newLevel, List<Upgrade> choices, Consumer<Upgrade> onChoice) {
        this.newLevel = newLevel;
        this.choices  = choices;
        this.onChoice = onChoice;
        this.visible  = true;
        this.hoveredCard = -1;
    }

    public void hide() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public MouseAdapter getMouseAdapter() {
        return mouseAdapter;
    }

    public void render(Graphics g) {
        if (!visible || choices == null) return;

        // Dim background
        g.setColor(BG_OVERLAY);
        g.fillRect(0, 0, screenW, screenH);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(TITLE_COLOR);
        String title = "LIVELLO " + newLevel + "  —  Scegli un potenziamento";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (screenW - fm.stringWidth(title)) / 2, screenH / 2 - 100);

        // Cards
        int totalW = choices.size() * CARD_W + (choices.size() - 1) * CARD_GAP;
        int startX = (screenW - totalW) / 2;
        int cardY  = screenH / 2 - 60;

        for (int i = 0; i < choices.size(); i++) {
            int cx = startX + i * (CARD_W + CARD_GAP);
            drawCard(g, choices.get(i), cx, cardY, i == hoveredCard);
        }
    }

    private void drawCard(Graphics g, Upgrade u, int cx, int cy, boolean hovered) {
        // Background
        g.setColor(hovered ? CARD_HOVER : CARD_DEFAULT);
        g.fillRoundRect(cx, cy, CARD_W, CARD_H, 16, 16);

        // Border
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(CARD_BORDER);
        g2d.setStroke(new BasicStroke(hovered ? 3 : 1));
        g2d.drawRoundRect(cx, cy, CARD_W, CARD_H, 16, 16);
        g2d.setStroke(new BasicStroke(1));

        // Name
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        FontMetrics fmN = g.getFontMetrics();
        g.drawString(u.getName(), cx + (CARD_W - fmN.stringWidth(u.getName())) / 2, cy + 30);

        // Description (word-wrapped)
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(new Color(200, 200, 200));
        drawWrapped(g, u.getDescription(), cx + 10, cy + 55, CARD_W - 20, 16);

        // Stack info
        if (u.getMaxStacks() > 1) {
            String stacks = u.getStackCount() + "/" + u.getMaxStacks();
            g.setFont(new Font("Arial", Font.ITALIC, 11));
            g.setColor(new Color(180, 180, 100));
            FontMetrics fmS = g.getFontMetrics();
            g.drawString(stacks, cx + CARD_W - fmS.stringWidth(stacks) - 8, cy + CARD_H - 10);
        }
    }

    private void drawWrapped(Graphics g, String text, int x, int y, int maxW, int lineH) {
        FontMetrics fm = g.getFontMetrics();
        String[] words  = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (fm.stringWidth(line + word) > maxW) {
                g.drawString(line.toString().trim(), x, y);
                y += lineH;
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        if (!line.isEmpty()) g.drawString(line.toString().trim(), x, y);
    }

    private int cardAt(int mx, int my) {
        if (choices == null) return -1;
        int totalW = choices.size() * CARD_W + (choices.size() - 1) * CARD_GAP;
        int startX = (screenW - totalW) / 2;
        int cardY  = screenH / 2 - 60;
        for (int i = 0; i < choices.size(); i++) {
            int cx = startX + i * (CARD_W + CARD_GAP);
            if (mx >= cx && mx <= cx + CARD_W && my >= cardY && my <= cardY + CARD_H) return i;
        }
        return -1;
    }
}
