package it.unibo.bioassault.model.viruses;

import it.unibo.bioassault.model.Game;
import it.unibo.bioassault.model.GameObject;
import it.unibo.bioassault.model.Handler;
import it.unibo.bioassault.model.ID;
import it.unibo.bioassault.model.player.Player;
import it.unibo.bioassault.model.stats.RunStats;

import java.util.Random;

public abstract class Virus extends GameObject {

    protected final Handler handler;
    protected final Random r = new Random();

    protected float mx;
    protected float my;
    protected Player player;
    protected RunStats stats;

    protected int hp;
    protected int maxHp;
    protected float speed;
    protected int choose = 0;

    private boolean isBig;
    private boolean dead;

    public Virus(final int x, final int y, final ID id,
                 final Handler handler, final int hp, final float speed) {
        super(x, y, id);
        this.handler = handler;
        this.hp = hp;
        this.maxHp = hp;
        this.speed = speed;
        this.dead = false;

        velX = r.nextInt(7) - 3;
        velY = r.nextInt(7) - 3;
        if (velX == 0 && velY == 0) {
            velX = 1;
        }
    }

    /**
     * Metodo comune per inseguire il player.
     */
    protected void trackPlayer() {
        final GameObject target = getPlayer();
        if (target == null) {
            velX = 0;
            velY = 0;
            return;
        }

        final Player p = (Player) target;

        if (!p.hasStartedMoving) {
            velX = 0;
            velY = 0;
            return;
        }

        float dx = p.getX() - x;
        float dy = p.getY() - y;
        final float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length != 0) {
            dx /= length;
            dy /= length;
        }

        velX = dx * this.speed;
        velY = dy * this.speed;
    }

    /**
     * Movimento comune con rimbalzo sui bordi.
     */
    protected void updateMovementAndBounds() {
        x += velX;
        y += velY;

        choose = r.nextInt(10);
        if (choose == 0) {
            velX = r.nextInt(7) - 3;
            velY = r.nextInt(7) - 3;
            if (velX == 0 && velY == 0) {
                velX = 1;
            }
        }

        if (x <= 0 || x >= Game.WORLD_WIDTH - 32) {
            velX *= -1;
        }
        if (y <= 0 || y >= Game.WORLD_HEIGHT - 32) {
            velY *= -1;
        }
    }

    public final void update() {
        final GameObject target = getPlayer();
        if (target != null) {
            mx = target.getX();
            my = target.getY();
            if (target instanceof Player) {
                this.player = (Player) target;
            }
        }
    }

    public void reachTarget() {
        this.setX(this.getX() + this.velX);
        this.setY(this.getY() + this.velY);

        final float angle = (float) Math.atan2(
                my - this.getY() + 8,
                mx - this.getX() + 4
        );

        this.velX = (float) (this.speed * Math.cos(angle));
        this.velY = (float) (this.speed * Math.sin(angle));
    }

    protected GameObject getPlayer() {
        if (handler == null || handler.object == null) {
            return null;
        }

        for (final GameObject obj : handler.object) {
            if (obj.getId() == ID.Player) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Posizione iniziale casuale attorno al giocatore.
     */
    public final void setStartingPosition(final float minDistance, final float maxDistance) {
        final GameObject target = getPlayer();
        final float px = (target != null) ? target.getX() : 0;
        final float py = (target != null) ? target.getY() : 0;

        final float angle = (float) (Math.random() * 2 * Math.PI);
        final float distance = minDistance + r.nextFloat() * (maxDistance - minDistance);

        this.x = px + (float) (distance * Math.cos(angle));
        this.y = py + (float) (distance * Math.sin(angle));
    }

    public void setIsBig(final boolean isBig) {
        if (isBig && !this.isBig) {
            this.hp *= 2;
            this.maxHp *= 2;
        }
        this.isBig = isBig;
    }

    public boolean isBig() {
        return this.isBig;
    }

    public void setStats(final RunStats stats) {
        this.stats = stats;
    }

    public void takeDamage(final int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }

        if (dead) {
            return;
        }

        this.hp = Math.max(0, this.hp - damage);

        if (this.hp == 0) {
            die();
        }
    }

    protected void die() {
        if (dead) {
            return;
        }

        dead = true;

        if (stats != null) {
            stats.recordKill(this.getClass().getSimpleName(), this.isBig(), false);
        }

        if (handler != null) {
            handler.removeObject(this);
        }
    }

    public int getHp() {
        return this.hp;
    }

    public int getMaxHp() {
        return this.maxHp;
    }

    public float getSpeed() {
        return this.speed;
    }

    public boolean isDead() {
        return dead || this.hp <= 0;
    }
}