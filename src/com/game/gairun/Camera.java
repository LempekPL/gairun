package com.game.gairun;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Camera {
    private double x = 0, y = 0, viewportScale = 2;
    private final int viewportWidth;
    private final int viewportHeight;
    private final Game game;
    private boolean debug = false;
    private BufferedImage clearer;
    private final int cameraMovementLimit = 30;

    public Camera(int viewportWidth, int viewportHeight, Game game) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.game = game;
        this.clearer = new BufferedImage(viewportWidth, viewportHeight, BufferedImage.TYPE_INT_RGB);
    }

    public void tick() {
        double playerX = game.getPlayer().getX();
        double playerY = game.getPlayer().getY();
        if (x + cameraMovementLimit < playerX || x - cameraMovementLimit > playerX) {
            x += (playerX - x) / cameraMovementLimit;
        }
        if (y + cameraMovementLimit < playerY || y - cameraMovementLimit > playerY) {
            y += (playerY - y) / cameraMovementLimit;
        }
        if (game.getPlayer().getSideMultiplier() != 0 && viewportScale >= 1.5) {
            viewportScale -= 0.001;
        } else {
            if (viewportScale < 2) {
                viewportScale += 0.005;
            }
        }
    }

    public void render() {
        BufferStrategy bs = game.getBufferStrategy();
        if (bs == null) {
            game.createBufferStrategy(3);
            return;
        }
        Graphics bsGraphics = bs.getDrawGraphics();
        BufferedImage screen = new BufferedImage(viewportWidth, viewportHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = screen.createGraphics();
        bsGraphics.drawImage(clearer,0,0,game);
        // render //////////////////////////////

        game.getPlayer().render(g, this);
        game.getMapController().render(g, this);

        // render //////////////////////////////
        // debug cam
        if (debug) {
            // camera move to limit
            g.setColor(Color.yellow);
            g.drawRect(viewportWidth / 2 - cameraMovementLimit, viewportHeight / 2 - cameraMovementLimit, cameraMovementLimit * 2, cameraMovementLimit * 2);
        }
        // draw virtual image to camera
        int offsetX = (int) -(viewportWidth * (viewportScale - 1));
        int offsetY = (int) -(viewportHeight * (viewportScale - 1));
        bsGraphics.drawImage(screen, offsetX, offsetY, (int) (viewportWidth * viewportScale), (int) (viewportHeight * viewportScale), 0, 0, viewportWidth, viewportHeight, null);
        // fps counter
        bsGraphics.setColor(Color.green);
        bsGraphics.drawString(game.getLastFrames()+"FPS",0,10);
        g.dispose();
        bsGraphics.dispose();
        bs.show();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getViewportScale() {
        return viewportScale;
    }

    public void setViewportScale(double viewportScale) {
        this.viewportScale = viewportScale;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void centerOnPlayer() {
        x = game.getPlayer().getX();
        y = game.getPlayer().getY();
    }
}
