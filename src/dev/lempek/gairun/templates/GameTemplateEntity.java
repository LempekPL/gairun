package dev.lempek.gairun.templates;

import dev.lempek.gairun.Game;
import dev.lempek.gairun.instances.Texture;
import dev.lempek.gairun.interfaces.GameEntity;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Map;

public abstract class GameTemplateEntity implements GameEntity {
    protected final Game game;
    // movement
    protected float x;
    protected float y;
    protected float velX;
    protected float velY;
    protected float acc;
    protected float dcc;
    protected float mass;
    // collisions
    protected Rectangle hitBox;
    protected boolean collisions;
    // options
    protected boolean lookingLeft = false;
    protected boolean invisible = false;
    // textures
    private Map<String, Texture> textureMap = null;
    private String currentTexture;

    public GameTemplateEntity(Game game, float x, float y, float mass) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.mass = mass;
    }

    public void render(Graphics g) {
        if (invisible || textureMap == null) return;
//        float xRender = x - (float) textureMap.get(currentTexture).getTexture().getWidth() / 2 + (float) Game.WIDTH / 2;
//        float yRender = y - (float) textureMap.get(currentTexture).getTexture().getHeight() + (float) Game.HEIGHT / 2;
//        g.drawImage(imageFlip(textureMap.get(currentTexture).getTexture()), (int) xRender, (int) yRender, null);
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

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public Rectangle getBounds() {
        if (collisions) {
            return hitBox;
        }
        return new Rectangle(0, 0, 0, 0);
    }

    public boolean isCollisions() {
        return collisions;
    }

    public void setCollisions(boolean collisions) {
        this.collisions = collisions;
    }

    protected BufferedImage imageFlip(BufferedImage image) {
        if (lookingLeft) {
            AffineTransform at = new AffineTransform();
            at.concatenate(AffineTransform.getScaleInstance(-1, 1));
            at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
            BufferedImage flippedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = flippedImage.createGraphics();
            g.transform(at);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            return flippedImage;
        } else {
            return image;
        }
    }
}
