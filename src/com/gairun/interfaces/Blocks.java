package com.gairun.interfaces;

import com.gairun.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Blocks {
    private final Texture tex;
    private BlockType blockType;
    private float x;
    private float y;
    private int[] blockHitbox;
    private final Game game;

//    public Blocks(BlockType blockType) {
//        this.blockType = blockType;
//    }

    public Blocks(float x, float y, int width, int height, Texture tex, BlockType blockType, Game game) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.blockType = blockType;
        this.blockHitbox = new int[]{0, width, 0, height};
        this.game = game;
    }

    public void render(Graphics g) {
        if (x < game.getCamera().getX() + (float) (Game.WIDTH/2) && x > game.getCamera().getX() - (float) (Game.WIDTH/2) - 16 && y < game.getCamera().getY() + (float) (Game.HEIGHT/2) + 16 && y > game.getCamera().getY() - (float) (Game.HEIGHT/2)) {
            float xRender = x + (float) Game.WIDTH / 2;
            float yRender = -y + (float) Game.HEIGHT / 2;
            g.drawImage(tex.getTexture(), (int) xRender, (int) yRender, null);
            if (game.getCamera().isDebug()) {
                g.setColor(Color.red);
                Rectangle2D mainHitbox = getHitbox();
                g.drawRect((int) mainHitbox.getX() + Game.WIDTH / 2, (int) -mainHitbox.getY() + Game.HEIGHT / 2, (int) mainHitbox.getWidth(), (int) mainHitbox.getHeight());
            }
        }
    }

    public Rectangle2D getHitbox() {
        return new Rectangle2D.Float(x - blockHitbox[2], y + blockHitbox[0], blockHitbox[2] + blockHitbox[3], blockHitbox[0] + blockHitbox[1]);
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public void setBlockType(BlockType blockType) {
        this.blockType = blockType;
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
}
