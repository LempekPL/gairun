package com.game.gairun;

import com.game.gairun.interfaces.Blocks;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class Player {
    private final Game game;
    private final BufferedImage tex;
    private float x, y;
    private float velX, velY;
    private float acc = 0.1F, dcc = 0.05F;
    private int jumps = 1;
    private boolean flying = false;

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
        if (flying) {
            if (inputs.contains(KeyEvent.VK_W)) {
                velY += acc;
            } else if (inputs.contains(KeyEvent.VK_S)) {
                velY -= acc;
            } else if (!inputs.contains(KeyEvent.VK_W) && !inputs.contains(KeyEvent.VK_S)) {
                if (velY > 0) velY -= dcc;
                else if (velY < 0) velY += dcc;
                if (velY > -0.1 && velY < 0.1) velY = 0;
            }
        } else {
            if (game.getKeyListener().checkKey(KeyEvent.VK_W) && jumps > 0) {
                velY = 4;
//                jumps--;
            }
            velY -= 0.1;
        }

        velX = clamp(velX, -3.5F, 3.5F);
        velY = clamp(velY, -5, 5);
    }

    public void render(Graphics g) {
        float xRender = x + (float) Game.WIDTH / 2 - game.getCamera().getX();
        float yRender = -(y + tex.getHeight()) + (float) Game.HEIGHT / 2 + game.getCamera().getY();
        g.drawImage(tex, (int) xRender, (int) yRender, null);
        if (game.getCamera().isDebug()) {
            g.drawString("Ax: %s, Ay: %s".formatted(x, y), (int) xRender, (int) yRender - 30);
            g.drawString("x: %s, y: %s".formatted(x - tex.getWidth() / 2, y + tex.getHeight()), (int) xRender, (int) yRender - 20);
            g.drawString("%s".formatted(velY), (int) xRender, (int) yRender - 10);
            g.setColor(Color.red);
            g.drawRect((int) xRender, (int) yRender, tex.getWidth() - 1, tex.getHeight() - 1);
            g.setColor(Color.cyan);
            float xRenderA = getHitboxX().x + (float) Game.WIDTH / 2 - game.getCamera().getX();
            float yRenderA = -getHitboxX().y + (float) Game.HEIGHT / 2 + game.getCamera().getY();
            g.drawRect((int) xRenderA, (int) yRenderA, getHitboxX().width, getHitboxX().height);
            g.setColor(Color.orange);
            float xRenderB = getHitboxY().x + (float) Game.WIDTH / 2 - game.getCamera().getX();
            float yRenderB = -getHitboxY().y + (float) Game.HEIGHT / 2 + game.getCamera().getY();
            g.drawRect((int) xRenderB, (int) yRenderB, getHitboxX().width, getHitboxX().height);
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
                }
                if (getHitboxY().intersects(block.getHitbox())) {
                    if (velY > 0) {
                        velY = 0;
                        y = block.getY() - 16 - tex.getHeight();
                    } else if (velY < 0) {
                        velY = 0;
                        y = block.getY();
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
