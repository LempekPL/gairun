package dev.lempek.gairun.templates;

import dev.lempek.gairun.Game;
import dev.lempek.gairun.instances.Texture;
import dev.lempek.gairun.interfaces.GameEntity;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class GameTemplateBlock implements GameEntity {
    protected final Game game;
    // placement
    protected float x;
    protected float y;
    // collisions
    protected Rectangle hitBox;
    protected boolean collisions;
    // textures
//    protected final Texture tex;

    public GameTemplateBlock(Game game, float x, float y, String tex, int[] size, int[] offset) {
        this.game = game;
        this.x = x;
        this.y = y;
//        tex = new Texture();
        if (size != null) {
            hitBox = new Rectangle((int) x + offset[0], (int) y + offset[1], size[0], size[1]);
        }
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
