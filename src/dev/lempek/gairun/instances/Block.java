package dev.lempek.gairun.templates;

import dev.lempek.gairun.Game;
import dev.lempek.gairun.instances.Texture;
import dev.lempek.gairun.interfaces.GameEntity;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Block implements GameEntity {
    private final Game game;
    // placement
    private float x;
    private float y;
    // collisions
    private Rectangle hitBox;
    private boolean collisions;
    // textures
//    private final Texture tex;

    public Block(Game game, float x, float y, String tex, int[] size, int[] offset) {
        this.game = game;
        this.x = x;
        this.y = y;
//        tex = new Texture();
        if (size != null) {
            hitBox = new Rectangle((int) x + offset[0], (int) y + offset[1], size[0], size[1]);
        }
    }

    public void tick() {
        // TODO: send ticks to scripts
    }

    public void render(Graphics g) {
        if (x < game.getCamera().getX() + (float) (Game.WIDTH/2) && x > game.getCamera().getX() - (float) (Game.WIDTH/2) - 16 && y < game.getCamera().getY() + (float) (Game.HEIGHT/2) + 16 && y > game.getCamera().getY() - (float) (Game.HEIGHT/2)) {
            float xRender = x + (float) Game.WIDTH / 2;
            float yRender = y + (float) Game.HEIGHT / 2;
//            g.drawImage(tex.getTexture(), (int) xRender, (int) yRender, null);
            if (game.getCamera().isDebug() && !(getHitbox().getWidth() <= 1 && getHitbox().getHeight() <= 1)) {
                g.setColor(Color.red);
                Rectangle2D mainHitbox = getHitbox();
                g.drawRect((int) mainHitbox.getX() + Game.WIDTH / 2, (int) mainHitbox.getY() + Game.HEIGHT / 2, (int) mainHitbox.getWidth(), (int) mainHitbox.getHeight());
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    public Rectangle2D getHitbox() {
        return hitBox;
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
