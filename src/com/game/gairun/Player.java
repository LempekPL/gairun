package com.game.gairun;

import com.game.gairun.interfaces.MapClass;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Player {
    private final BufferedImage tex;
    protected double x, y;
    private double acc = 1, dcc = 0.5, velX = 0, velY = 0, friction = 0;
    private int sideMultiplier = 0;
    private int jumps = 1;
    private boolean onSurface = false, collisionTop = false, collisionBottom = false, collisionLeft = false, collisionRight = false, collision = false;
    private Game game;

    public Player(double x, double y, BufferedImage tex, Game game) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.game = game;
    }

    public void tick() {
        y += velY;
        x += velX * sideMultiplier;
        if (velX > 0) {
            velX -= friction;
        } else {
            velX = 0;
            friction = 0;
            sideMultiplier = 0;
        }

        velX = clamp(velX, -5, 5);
        velY = clamp(velY, -5, 5);


//        System.out.println(onSurface);
//        System.out.println("PLAYER Coords: "+x+ ", " + y);

    }

    public void render(Graphics g, Camera cam) {
        double xRender = ((x - (double) tex.getWidth() / 2) + ((double) cam.getViewportWidth() / 2)) - cam.getX();
        double yRender = (((y + tex.getHeight()) * -1) + (double) cam.getViewportHeight() / 2) + cam.getY();
//        System.out.println("PLAYER Rendered: "+xRender+ ", " + yRender);
        g.drawImage(tex, (int) xRender, (int) yRender, null);
        if (cam.isDebug()) {
            g.setColor(Color.red);
            g.drawRect((int) xRender, (int) yRender, tex.getWidth() - 1, tex.getHeight() - 1);
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

    public void resetPlayer() {
        velX = 0;
        velY = 0;
        x = 0;
        y = 0;
        sideMultiplier = 0;
        friction = 0;
        jumps = 1;
    }

    public double clamp(double number, double min, double max) {
        if (min > max) {
            return number;
        }
        if (number > max) return max;
        return Math.max(number, min);
    }
}
