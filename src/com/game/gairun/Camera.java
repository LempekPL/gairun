package com.game.gairun;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Camera {
    private double x, y, viewportScale;
    private int viewportWidth, viewportHeight;
    private Game game;
    private boolean debug = false;

    public Camera(int viewportWidth, int viewportHeight, Game game) {
        this.x = 0;
        this.y = 0;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.viewportScale = 1.25;
        this.game = game;
    }

    public void tick() {
        double playerX = game.getPlayer().getX();
        double playerY = game.getPlayer().getY();
        if (x+50 < playerX || x-50 > playerX) {
            x += (playerX - x - 48)/50;
        }
        if (y+50 < playerY || y-50 > playerY) {
            y += (playerY - y - 48)/50;
        }
        System.out.println("CAMERA Coords: "+x+ ", " + y);
    }

    public void render() {
        BufferStrategy bs = game.getBufferStrategy();
        if (bs == null) {
            game.createBufferStrategy(3);
            return;
        }
        Graphics bsGraphics = bs.getDrawGraphics();
        BufferedImage screen = new BufferedImage(viewportWidth, viewportHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = screen.createGraphics();
        // render //////////////////////////////

        game.getPlayer().render(g, this);

        // render //////////////////////////////
        g.dispose();
        int offsetX = (int) -(viewportWidth * (viewportScale-1));
        int offsetY = (int) -(viewportHeight * (viewportScale-1));
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
