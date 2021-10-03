package com.gairun;

import com.gairun.interfaces.Blocks;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class Player {
    private final Game game;
    private final BufferedImage tex;
    private final float acc = 0.15F;
    private final float dcc = 0.05F;
    List<Integer> inputs;
    private float x;
    private float y;
    private float velX;
    private float velY;
    private int jumps = 1;
    private boolean flying = false;
    private boolean invisible = false;
    private boolean blockedKeys = false;
    private boolean noclip = false;

    public Player(float x, float y, BufferedImage tex, Game game) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.game = game;
    }

    public void tick() {
        if (!noclip) {
            collisionCheck();
        }
        y += velY;
        x += velX;

        if (!blockedKeys) {
            keyCheck();
        }

        velX = clamp(velX, -4, 4);
        velY = clamp(velY, -8, 5);
    }

    public void render(Graphics g) {
        float xRender = x + (float) Game.WIDTH / 2;
        float yRender = -(y + tex.getHeight()) + (float) Game.HEIGHT / 2;
        if (!invisible) {
            g.drawImage(tex, (int) xRender, (int) yRender, null);
        }
        if (game.getCamera().isDebug()) {
            g.setColor(Color.red);
            g.drawRect((int) xRender, (int) yRender, tex.getWidth(), tex.getHeight());
        }
    }

    // this handles player collision in separate functions
    private void collisionCheck() {
        for (List<Blocks> blockRow : game.getMapController().getMapBlocks()) {
            for (Blocks block : blockRow) {
                // TODO: check if block is withing distance to remove unnecessary checks
                verticalCollision(block);
                horizontalCollision(block);
                wallJump(block);
            }
        }
    }

    private void horizontalCollision(Blocks block) {
        if (getHitboxWidth().intersects(block.getHitbox())) {
            if (velX > 0) {
                velX = 0;
                x = block.getX() - tex.getWidth();
            } else if (velX < 0) {
                velX = 0;
                x = block.getX() + block.getHitbox().width;
            }
            if (velY < 0 && !flying) {
                velY += 0.5;
                velY = clamp(velY, -8, -1);
            }
        }
    }

    private void verticalCollision(Blocks block) {
        if (getHitboxHeight().intersects(block.getHitbox())) {
            if (velY > 0) {
                velY = 0;
                y = block.getY() - 16 - tex.getHeight();
            } else if (velY < 0) {
                velY = 0;
                y = block.getY();
                jumps = 1;
            }
        }
    }

    private void wallJump(Blocks block) {
        if (!flying && jumps <= 0 && !game.isConsoleOpened()) {
            if (getWallRight().intersects(block.getHitbox()) && game.getKeyListener().checkKey(KeyEvent.VK_W)) {
                velY = 3.5f;
                velX = -2;
            }
            if (getWallLeft().intersects(block.getHitbox()) && game.getKeyListener().checkKey(KeyEvent.VK_W)) {
                velY = 3.5f;
                velX = 2;
            }
        }
    }

    // this handles player movement in separate functions
    private void keyCheck() {
        inputs = game.getKeyListener().getKeysPressed();
        horizontalMove(inputs);
        if (flying) {
            verticalMove(inputs);
        } else {
            jumping();
        }
    }

    // horizontal movement
    private void horizontalMove(List<Integer> inputs) {
        if (inputs.contains(KeyEvent.VK_D) && !game.isConsoleOpened()) {
            velX += acc;
        } else if (inputs.contains(KeyEvent.VK_A) && !game.isConsoleOpened()) {
            velX -= acc;
        } else if (!inputs.contains(KeyEvent.VK_D) && !inputs.contains(KeyEvent.VK_A)) {
            if (velX > 0) velX -= dcc;
            else if (velX < 0) velX += dcc;
            if (velX > -0.1 && velX < 0.1) velX = 0;
        }
    }

    // vertical movement, if flying is set to true
    private void verticalMove(List<Integer> inputs) {
        if (inputs.contains(KeyEvent.VK_W) && !game.isConsoleOpened()) {
            velY += acc;
        } else if (inputs.contains(KeyEvent.VK_S) && !game.isConsoleOpened()) {
            velY -= acc;
        } else if (!inputs.contains(KeyEvent.VK_W) && !inputs.contains(KeyEvent.VK_S)) {
            if (velY > 0) velY -= dcc;
            else if (velY < 0) velY += dcc;
            if (velY > -0.1 && velY < 0.1) velY = 0;
        }
    }

    // jumping with gravitational pull
    private void jumping() {
        if (jumps > 0 && game.getKeyListener().checkKey(KeyEvent.VK_W) && !game.isConsoleOpened()) {
            velY = 4;
            jumps--;
        }
        velY -= 0.11;
    }

    // clamps number between min and max
    private float clamp(float value, float min, float max) {
        if (value >= max) return max;
        else if (value <= min) return min;
        return value;
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

    public Rectangle getHitboxWidth() {
        return new Rectangle((int) (x + velX), (int) y + tex.getHeight(), tex.getWidth(), tex.getHeight());
    }

    public Rectangle getHitboxHeight() {
        return new Rectangle((int) x, (int) (y + velY + tex.getHeight()), tex.getWidth(), tex.getHeight());
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

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isBlockedKeys() {
        return blockedKeys;
    }

    public void setBlockedKeys(boolean blockedKeys) {
        this.blockedKeys = blockedKeys;
    }

    public boolean isNoclip() {
        return noclip;
    }

    public void setNoclip(boolean noclip) {
        this.noclip = noclip;
    }
}
