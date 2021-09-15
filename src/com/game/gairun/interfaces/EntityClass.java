package com.game.gairun.interfaces;

import java.awt.*;

public class EntityClass {
    protected double x, y, hitboxWidth, hitboxHeight;

    public EntityClass(double x, double y, double hitboxWidth, double hitboxHeight) {
        this.x = x;
        this.y = y;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
    }

    public Rectangle getBounds() {
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
