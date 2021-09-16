package com.game.gairun;

import java.awt.*;
import java.awt.image.BufferedImage;
// implements Entity
public class Player {
    protected double x, y, hitboxWidth, hitboxHeight;
    private double velX = 0, velY = 0, friction = 0;
    private final BufferedImage tex;
    private int sideMultiplier = 0;
    private int jumps = 1;
    private boolean onSurface = false;

    public Player(double x, double y, BufferedImage tex) {
        this.x = x;
        this.y = y;
        this.hitboxWidth = tex.getWidth();
        this.hitboxHeight = tex.getHeight();
        this.tex = tex;
    }

    public void tick() {
//        double preY = y;
        if (!onSurface) {
            velY -= 0.1;
        }
        y += velY;
        x += velX * sideMultiplier;
        if (velX > 0) {
            velX -= friction;
        } else {
            velX = 0;
            friction = 0;
            sideMultiplier = 0;
        }

        // temp
//        if (x < -500) x = -500;
//        if (x > 500) x = 500;
        if (y < 0) y = 0;
//        if (y > 50) y = 50;
        // TODO: collision

//        if (preY == y && velY > 0) {
//            onSurface = true;
            jumps = 2;
//        } else {
//            onSurface = false;
//        }
//        System.out.println(onSurface);
//        System.out.println("PLAYER Coords: "+x+ ", " + y);
    }

    public void render(Graphics g, Camera cam) {
        double xRender = ((x - (double) tex.getWidth()/2) + ((double) cam.getViewportWidth() / 2)) - cam.getX();
        double yRender = (((y+tex.getHeight())*-1) + (double) cam.getViewportHeight() / 2) + cam.getY();
//        System.out.println("PLAYER Rendered: "+xRender+ ", " + yRender);
        g.drawImage(tex, (int) xRender, (int) yRender, null);
        if (cam.isDebug()) {
            g.setColor(Color.red);
            g.drawRect((int) xRender, (int) yRender, tex.getWidth(), tex.getHeight());
        }
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public int getSideMultiplier() {
        return sideMultiplier;
    }

    public void setSideMultiplier(int sideMultiplier) {
        this.sideMultiplier = sideMultiplier;
    }

    public int getJumps() {
        return jumps;
    }

    public void setJumps(int jumps) {
        this.jumps = jumps;
    }

    public boolean isOnSurface() {
        return onSurface;
    }

    public void setOnSurface(boolean onSurface) {
        this.onSurface = onSurface;
    }

    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y, (int) hitboxHeight, (int) hitboxHeight);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getHitboxWidth() {
        return hitboxWidth;
    }

    public void setHitboxWidth(double hitboxWidth) {
        this.hitboxWidth = hitboxWidth;
    }

    public double getHitboxHeight() {
        return hitboxHeight;
    }

    public void setHitboxHeight(double hitboxHeight) {
        this.hitboxHeight = hitboxHeight;
    }
}
