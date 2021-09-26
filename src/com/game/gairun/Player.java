package com.game.gairun;

import com.game.gairun.interfaces.Blocks;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

public class Player {
    private final Game game;
    private final BufferedImage tex;
    private float x, y;
    private float velX, velY;
    private final float acc = 0.15F;
    private final float dcc = 0.05F;
    private int jumps = 1;
    private boolean flying = false;
    List<Integer> inputs;

    public Player(float x, float y, BufferedImage tex, Game game) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.game = game;
    }

    public void tick() {
        collision();
        y += velY;
        x += velX;

        inputs = game.getKeyListener().getKeysPressed();
        if (inputs.contains(KeyEvent.VK_D) && !game.isConsoleOpened()) {
            velX += acc;
        } else if (inputs.contains(KeyEvent.VK_A) && !game.isConsoleOpened()) {
            velX -= acc;
        } else if (!inputs.contains(KeyEvent.VK_D) && !inputs.contains(KeyEvent.VK_A)) {
            if (velX > 0) velX -= dcc;
            else if (velX < 0) velX += dcc;
            if (velX > -0.1 && velX < 0.1) velX = 0;
        }
        if (flying) {
            if (inputs.contains(KeyEvent.VK_W) && !game.isConsoleOpened()) {
                velY += acc;
            } else if (inputs.contains(KeyEvent.VK_S) && !game.isConsoleOpened()) {
                velY -= acc;
            } else if (!inputs.contains(KeyEvent.VK_W) && !inputs.contains(KeyEvent.VK_S)) {
                if (velY > 0) velY -= dcc;
                else if (velY < 0) velY += dcc;
                if (velY > -0.1 && velY < 0.1) velY = 0;
            }
        } else {
            if (jumps > 0 && game.getKeyListener().checkKey(KeyEvent.VK_W)) {
                velY = 4;
                jumps--;
            }
            velY -= 0.1;
        }

        velX = clamp(velX, -4, 4);
        velY = clamp(velY, -8, 5);
    }

    public void render(Graphics g) {
        float xRender = x + (float) Game.WIDTH / 2 - game.getCamera().getX();
        float yRender = -(y + tex.getHeight()) + (float) Game.HEIGHT / 2 + game.getCamera().getY();
        g.drawImage(tex, (int) xRender, (int) yRender, null);
        if (game.getCamera().isDebug()) {
            g.setColor(Color.red);
            g.drawRect((int) xRender, (int) yRender, tex.getWidth() - 1, tex.getHeight() - 1);
            g.setColor(Color.green);
            Rectangle a = getBounds();
            g.drawRect((int) xRender + a.x, (int) yRender + a.y, a.width, a.height);
            g.setColor(Color.red);
        }
    }

    private float clamp(float value, float min, float max) {
        if (value >= max) value = max;
        else if (value <= min) value = min;
        return value;
    }

    private void collision() {
        for (List<Blocks> blockRow : game.getMapController().getMapBlocks()) {
            for (Blocks block : blockRow) {
                if (getHitboxX().intersects(block.getHitbox())) {
                    if (velX > 0) {
                        velX = 0;
                        x = block.getX() - tex.getWidth();
                    } else if (velX < 0) {
                        velX = 0;
                        x = block.getX() + block.getHitbox().width;
                    }
                    if (velY < 0) {
                        velY += 0.5;
                        velY = clamp(velY, -8, -2);
                    }
                }
                if (getHitboxY().intersects(block.getHitbox())) {
                    if (velY > 0) {
                        velY = 0;
                        y = block.getY() - 16 - tex.getHeight();
                    } else if (velY < 0) {
                        velY = 0;
                        y = block.getY();
                        jumps = 1;
                    }
                }
                if (getWallRight().intersects(block.getHitbox()) && !flying && jumps <= 0) {
                    if (game.getKeyListener().checkKey(KeyEvent.VK_W)) {
                        velY = 3.5f;
                        velX = -2;
                    }
                }
                if (getWallLeft().intersects(block.getHitbox()) && !flying && jumps <= 0) {
                    if (game.getKeyListener().checkKey(KeyEvent.VK_W)) {
                        velY = 3.5f;
                        velX = 2;
                    }
                }
            }
        }
    }

    public void spawnPlayer() {
        velX = 0;
        velY = 0;
        x = 0;
        y = 0;
        jumps = 1;
    }

    public void spawnPlayer(float x, float y) {
        velX = 0;
        velY = 0;
        this.x = x;
        this.y = y;
        jumps = 1;
    }

    public Rectangle getHitboxX() {
        return new Rectangle((int) (x + velX), (int) y + tex.getHeight(), tex.getWidth() + (int) (velX / 2), tex.getHeight());
    }

    public Rectangle getHitboxY() {
        return new Rectangle((int) x, (int) (y + tex.getHeight() + velY), tex.getWidth(), tex.getHeight() + (int) (velY / 2));
    }

    private Rectangle getWallLeft() {
        return new Rectangle((int) x - 2, (int) y + tex.getHeight(), tex.getWidth(), tex.getHeight());
    }

    private Rectangle getWallRight() {
        return new Rectangle((int) x, (int) y + tex.getHeight(), tex.getWidth() + 2, tex.getHeight());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }
}
