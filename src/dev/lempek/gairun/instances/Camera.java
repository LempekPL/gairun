package dev.lempek.gairun.instances;

import dev.lempek.gairun.Game;

public class Camera {
    private final Game game;
    private float x;
    private float y;
    private float scale = 2;
    private int cameraMovementThreshold = 30;
    private float cameraMovementSpeedMultiplier = 1.5f;
    private boolean debug = false;
    private boolean scaling = true;
    private boolean followingPlayer = true;

    public Camera(Game game, int x, int y) {
        this.game = game;
        this.x = x;
        this.y = y;
    }

    public void tick() {
        if (game.getPlayer() != null) {
            Player player = game.getPlayer();
            x = player.getX();
            y = player.getY();
//            if (followingPlayer) {
//                float playerX = player.getX();
//                float playerY = player.getY();
//                if (x - cameraMovementThreshold > playerX) {
//                    x += (playerX - x + cameraMovementThreshold) / cameraMovementThreshold;
//                } else if (x + cameraMovementThreshold < playerX) {
//                    x += (playerX - x - cameraMovementThreshold) / cameraMovementThreshold;
//                }
//                if (y - cameraMovementThreshold > playerY) {
//                    y += (playerY - y + cameraMovementThreshold) / cameraMovementThreshold;
//                } else if (y + cameraMovementThreshold < playerY) {
//                    y += (playerY - y - cameraMovementThreshold) / cameraMovementThreshold;
//                }
//            }
//            if (scaling) {
//                if (player.getVelX() != 0 || player.getVelY() > 2 || player.getVelY() < -2) {
//                    if (scale >= 1.5) {
//                        scale -= 0.002;
//                    }
//                } else {
//                    if (scale < 2) {
//                        scale += 0.005;
//                    }
//                }
//            }
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

    public int getCameraMovementThreshold() {
        return cameraMovementThreshold;
    }

    public void setCameraMovementThreshold(int cameraMovementThreshold) {
        this.cameraMovementThreshold = cameraMovementThreshold;
    }

    public float getCameraMovementSpeedMultiplier() {
        return cameraMovementSpeedMultiplier;
    }

    public void setCameraMovementSpeedMultiplier(float cameraMovementSpeedMultiplier) {
        this.cameraMovementSpeedMultiplier = cameraMovementSpeedMultiplier;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
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
