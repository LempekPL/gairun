package com.game.gairun;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Camera {
    private double x, y, viewportScale;
    private final int viewportWidth;
    private final int viewportHeight;
    private final Game game;
    private boolean debug = false;
    private BufferedImage clearer;

    public Camera(int viewportWidth, int viewportHeight, Game game) {
        this.x = 0;
        this.y = 0;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.viewportScale = 2;
        this.game = game;
        this.clearer = new BufferedImage(viewportWidth, viewportHeight, BufferedImage.TYPE_INT_RGB);
    }

    public void tick() {
        double playerX = game.getPlayer().getX();
        double playerY = game.getPlayer().getY();
        if (x + 20 < playerX || x - 20 > playerX) {
            x += (playerX - x - 19) / 20;
        }
        if (y + 20 < playerY || y - 20 > playerY) {
            y += (playerY - y - 19) / 20;
        }
        if (game.getPlayer().getSideMultiplier() > 0 && viewportScale >= 1.5) {
            viewportScale -= 0.005;
        } else if (game.getPlayer().getSideMultiplier() < 0 && viewportScale >= 1.5) {
            viewportScale -= 0.005;
        } else {
            if (viewportScale < 2) {
                viewportScale += 0.01;
            }
        }
//        System.out.println("CAMERA SCALE: " + viewportScale);
//        System.out.println("CAMERA Coords: " + x + ", " + y);
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

        // fps counter
        bsGraphics.setColor(Color.green);
        bsGraphics.drawString(game.getLastFrames()+"FPS",0,10);

        // render //////////////////////////////
        g.dispose();
        int offsetX = (int) -(viewportWidth * (viewportScale - 1));
        int offsetY = (int) -(viewportHeight * (viewportScale - 1));
        bsGraphics.drawImage(screen, offsetX, offsetY, (int) (viewportWidth * viewportScale), (int) (viewportHeight * viewportScale), 0, 0, viewportWidth, viewportHeight, null);
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
}
