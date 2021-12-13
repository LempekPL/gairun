package dev.lempek.gairun.instances;

import dev.lempek.gairun.Game;

public class Camera {
    private final Game game;
    private float x;
    private float y;
    private float scale = 2;
    private int cameraMovementThreshold = 30;
    private float cameraMovementSpeedMultiplier = 1.5f;
    private minScale =
    private boolean debug = false;
    private boolean scaling = true;
    private boolean followingPlayer = true;

    public Camera(int x, int y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;
    }

    public void tick() {
        if (game.getPlayer() != null) {
            Player player = game.getPlayer();
            if (followingPlayer) {
                float playerX = player.getX();
                float playerY = player.getY();
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
                if (player.getVelX() != 0 || player.getVelY() > 2 || player.getVelY() < -2) {
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
    }

    public void centerOnPlayer() {
        x = game.getPlayer().getX();
        y = game.getPlayer().getY();
    }
}
