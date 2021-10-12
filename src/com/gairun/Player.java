package com.gairun;

import com.gairun.interfaces.Blocks;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class Player {
    private final Game game;
    private final BufferedImage tex;
    private final float acc = 0.15F;
    private final float dcc = 0.05F;
    // top, bottom, left, right
    private final int[] playerHitbox = new int[]{32, 0, 7, 7};
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

        if (flying) {
            velX = clamp(velX, -4, 4);
            velY = clamp(velY, -4, 4);
        }
        velX = clamp(velX, -4, 4);
        velY = clamp(velY, -8, 5);
    }

    public void render(Graphics g) {
        float xRender = x - (float) tex.getWidth() / 2 + (float) Game.WIDTH / 2;
        float yRender = -(y + tex.getHeight()) + (float) Game.HEIGHT / 2;
        if (!invisible) {
            g.drawImage(tex, (int) xRender, (int) yRender, null);
        }
        if (game.getCamera().isDebug()) {
            g.setColor(Color.red);
            Rectangle2D mainHitbox = getHitbox();
            g.drawRect((int) (x - mainHitbox.getWidth() / 2) + Game.WIDTH / 2, (int) (-y - mainHitbox.getHeight() + playerHitbox[1]) + Game.HEIGHT / 2, (int) mainHitbox.getWidth(), (int) mainHitbox.getHeight());
        }
    }

    private void drawRectangle(Graphics g, Rectangle2D rect) {
        float xRender = (float) (rect.getX() + Game.WIDTH / 2);
        float yRender = (float) (-rect.getY() + Game.HEIGHT / 2);
        g.drawRect((int) xRender, (int) yRender, (int) rect.getWidth(), (int) rect.getHeight());
    }

    // this handles player collision in separate functions
    private void collisionCheck() {
        for (List<Blocks> blockRow : game.getMapController().getMapBlocks()) {
            for (Blocks block : blockRow) {
                // TODO: check if block is withing distance to remove unnecessary checks
                horizontalCollision(block);
                verticalCollision(block);
                wallJump(block);
            }
        }
    }

    private void horizontalCollision(Blocks block) {
        if (getHitboxWidth().intersects(block.getHitbox())) {
            if (velX > 0) {
                velX = 0;
                x = block.getX() - (float) getHitbox().getWidth() / 2;
            } else if (velX < 0) {
                velX = 0;
                x = (float) (block.getX() + block.getHitbox().getWidth() + getHitbox().getWidth() / 2);
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
                y = (float) (block.getY() - block.getHitbox().getHeight() - getHitbox().getHeight());
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
        x = 8;
        y = 0;
        jumps = 1;
    }

    public void spawnPlayer(float x, float y) {
        velX = 0;
        velY = 0;
        this.x = x + 8;
        this.y = y;
        jumps = 1;
    }

//    public Rectangle getHitbox() {
//        return new Rectangle((int) x - playerHitboxOffset[2], (int) y + tex.getHeight() + playerHitboxOffset[0], tex.getWidth() + playerHitboxOffset[2] + playerHitboxOffset[3], tex.getHeight() + playerHitboxOffset[0] + playerHitboxOffset[1]);
//    }

    public Rectangle2D getHitbox() {
        return new Rectangle2D.Float(x - playerHitbox[2], y + (float) playerHitbox[0] / 2, playerHitbox[2] + playerHitbox[3], playerHitbox[0] + playerHitbox[1]);
    }

    public Rectangle2D getHitboxWidth() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Float((float) hitbox.getX() + velX, (float) hitbox.getY() + playerHitbox[1], (float) hitbox.getWidth(), (float) hitbox.getHeight());
    }

    public Rectangle2D getHitboxHeight() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Float((float) hitbox.getX(), (float) hitbox.getY() + velY + playerHitbox[1], (float) hitbox.getWidth(), (float) hitbox.getHeight());
    }

    private Rectangle2D getWallLeft() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Float((float) hitbox.getX() - 2, (float) hitbox.getY() + playerHitbox[1], 2, (float) hitbox.getHeight());
    }

    private Rectangle2D getWallRight() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Float((float) (hitbox.getX() + hitbox.getWidth()), (float) hitbox.getY() + playerHitbox[1], 2, (float) hitbox.getHeight());
    }

    private Rectangle2D getStanding() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Float((float) hitbox.getX(), (float) (hitbox.getY() - hitbox.getHeight()) + playerHitbox[1], (float) hitbox.getWidth(), 2);
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
