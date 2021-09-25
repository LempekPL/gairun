package com.game.gairun;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Camera {
    private float x, y, scale = 2;
    private final Game game;
    private boolean debug = false;
    private final int cameraMovementLimit = 30;

    public Camera(int x, int y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;
    }

    public void tick() {
        if (game.getKeyListener().checkKey(KeyEvent.VK_F7)) debug = !debug;
        float playerX = game.getPlayer().getX();
        float playerY = game.getPlayer().getY();
        if (x - cameraMovementLimit > playerX) {
            x += (playerX - x + cameraMovementLimit) / cameraMovementLimit;
        } else if (x + cameraMovementLimit < playerX) {
            x += (playerX - x - cameraMovementLimit) / cameraMovementLimit;
        }
        if (y - cameraMovementLimit > playerY) {
            y += (playerY - y + cameraMovementLimit) / cameraMovementLimit;
        } else if (y + cameraMovementLimit < playerY) {
            y += (playerY - y - cameraMovementLimit) / cameraMovementLimit;
        }
        if (game.getPlayer().getVelX() != 0 && scale >= 1.8) {
            scale -= 0.002;
        }
        if (scale < 2) {
            scale += 0.005;
        }
    }

    public void centerOnPlayer() {
        x = game.getPlayer().getX();
        y = game.getPlayer().getY();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getCameraMovementLimit() {
        return cameraMovementLimit;
    }
}

