package com.gairun;

public class Camera {
    private final Game game;
    private final int cameraMovementLimit = 30;
    private float x;
    private float y;
    private float scale = 2;
    private boolean debug = false;
    private boolean scaling = true;
    private boolean followingPlayer = true;

    public Camera(int x, int y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;
    }

    public void tick() {
        if (followingPlayer) {
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
        }
        if (scaling) {
            if (game.getPlayer().getVelX() != 0 || game.getPlayer().getVelY() > 2 || game.getPlayer().getVelY() < -2) {
                if (scale >= 1.5) {
                    scale -= 0.002;
                }
            } else {
                if (scale < 2) {
                    scale += 0.005;
                }
            }
        }
    }

    public void centerOnPlayer() {
        x = game.getPlayer().getX();
        y = game.getPlayer().getY();
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

    public boolean isScaling() {
        return scaling;
    }

    public void setScaling(boolean scaling) {
        this.scaling = scaling;
    }

    public boolean isFollowingPlayer() {
        return followingPlayer;
    }

    public void setFollowingPlayer(boolean followingPlayer) {
        this.followingPlayer = followingPlayer;
    }
}

