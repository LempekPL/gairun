package com.game.gairun;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class Player {
    private final Game game;
    private final BufferedImage tex;
    protected float x, y;
    protected float velX, velY;
    private float acc = 0.1F, dcc = 0.05F;
    private int jumps = 1;
//    private boolean onSurface = false, collisionTop = false, collisionBottom = false, collisionLeft = false, collisionRight = false, collision = false;

    public Player(float x, float y, BufferedImage tex, Game game) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.game = game;
    }

    public void tick() {
        y += velY;
        x += velX;
        if (y > 0) {
            velY -= 0.1;
        }

        List<Integer> inputs = game.getKeyListener().getKeysPressed();
        if (inputs.contains(KeyEvent.VK_D)) {
            velX += acc;
        } else if (inputs.contains(KeyEvent.VK_A)) {
            velX -= acc;
        } else if (!inputs.contains(KeyEvent.VK_D) && !inputs.contains(KeyEvent.VK_A)) {
            if (velX > 0) velX -= dcc;
            else if (velX < 0) velX += dcc;
            if (velX > -0.1 && velX < 0.1) velX = 0;
        }
        // temp
        if (y < 0) y = 0;

        if (game.getKeyListener().checkKey(KeyEvent.VK_W) && jumps > 0) {
            velY = 4;
            jumps--;
        }

        velX = clamp(velX, -3.5F, 3.5F);
        velY = clamp(velY, -5, 5);
    }

    public void render(Graphics g) {
        float xRender = x - (float) tex.getWidth() / 2 + (float) Game.WIDTH / 2 - game.getCamera().getX();
        float yRender = -(y + tex.getHeight()) + (float) Game.HEIGHT / 2 + game.getCamera().getY();
        g.drawImage(tex, (int) xRender, (int) yRender, null);
        if (game.getCamera().isDebug()) {
            g.setColor(Color.red);
            g.drawRect((int) xRender, (int) yRender, tex.getWidth() - 1, tex.getHeight() - 1);
        }
    }

    public float clamp(float value, float min, float max) {
        if (value >= max) value = max;
        else if (value <= min) value = min;
        return value;
    }

    public void resetPlayer() {
        velX = 0;
        velY = 0;
        x = 0;
        y = 0;
        jumps = 1;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public float getAcc() {
        return acc;
    }

    public void setAcc(float acc) {
        this.acc = acc;
    }

    public float getDcc() {
        return dcc;
    }

    public void setDcc(float dcc) {
        this.dcc = dcc;
    }

    public int getJumps() {
        return jumps;
    }

    public void setJumps(int jumps) {
        this.jumps = jumps;
    }
}
