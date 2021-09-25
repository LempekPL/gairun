package com.game.gairun.interfaces;

import com.game.gairun.Game;

import java.awt.*;

public class Blocks {
    private Texture tex;
    private BlockType blockType;
    private float x, y;
    private Game game;

//    public Blocks(BlockType blockType) {
//        this.blockType = blockType;
//    }

    public Blocks(float x, float y, Texture tex, BlockType blockType, Game game) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.blockType = blockType;
        this.game = game;
    }

    public void render(Graphics g) {
        float xRender = x + (float) Game.WIDTH / 2 - game.getCamera().getX();
        float yRender = -y + (float) Game.HEIGHT / 2 + game.getCamera().getY();
        g.drawImage(tex.getTexture(), (int) xRender, (int) yRender, null);
        if (game.getCamera().isDebug()) {
            g.setColor(Color.red);
            g.drawRect((int) xRender, (int) yRender, tex.getTexture().getWidth() - 1, tex.getTexture().getHeight() - 1);
        }
    }

    public void tick() {
        tex.runAnimation();
    }

    public Rectangle getHitbox() {
        // TODO: custom hitbox size
        return new Rectangle((int) x, (int) y + tex.getTexture().getHeight(), tex.getTexture().getWidth(), tex.getTexture().getHeight());
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
