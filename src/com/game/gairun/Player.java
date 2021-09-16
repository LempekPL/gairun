package com.game.gairun;

import com.game.gairun.interfaces.Entity;
import com.game.gairun.interfaces.EntityClass;

import java.awt.*;
import java.awt.image.BufferedImage;
// implements Entity
public class Player extends EntityClass {
    private double velX = 0, velY = 0, friction = 0;
    private final BufferedImage tex;
    private int sideMultiplier = 0;
    private int jumps = 1;
    private boolean onSurface = false;

    public Player(double x, double y, BufferedImage tex) {
        super(x, y, tex.getWidth(), tex.getHeight());
        this.tex = tex;
    }

    public void tick() {
        double preY = y;
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
        if (x < -500) x = -500;
        if (x > 500) x = 500;
        if (y < 0) y = 0;
        if (y > 500) y = 500;
        // TODO: collision

//        if (preY == y && velY > 0) {
//            onSurface = true;
            jumps = 2;
//        } else {
//            onSurface = false;
//        }
//        System.out.println(onSurface);
    }

    public void render(Graphics g, Camera cam) {
        double xRender = (x + ((double) cam.getViewportWidth() / 2)) + cam.getX();
        double yRender = ((y*-1) + (double) cam.getViewportHeight() / 2) + cam.getY();
//        System.out.println("Rendered: "+xRender+ ", " + yRender);
//        System.out.println("Coords: "+x+ ", " + y);
        g.drawImage(tex, (int) xRender, (int) yRender, null);
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
}
