package com.game.gairun;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Camera {
    private double x, y, viewportScale;
    private int viewportWidth, viewportHeight;
    // image to "clear" displayed data
    private final BufferedImage image;

    public Camera(int viewportWidth, int viewportHeight) {
        this.x = 0;
        this.y = 0;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.viewportScale = 5;
        this.image = new BufferedImage(viewportWidth, viewportHeight, BufferedImage.TYPE_INT_RGB);
    }

    public void tick() {

    }

    public void render(Game game) {
        BufferStrategy bs = game.getBufferStrategy();
        if (bs == null) {
            game.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        // "clear" screen
        g = g.create(0, 0, (int) (viewportWidth * viewportScale), (int) (viewportHeight * viewportScale));
        g.drawImage(image, 0, 0, viewportWidth * (int) viewportScale, viewportHeight * (int) viewportScale, game);
        // render //////////////////////////////

//        Image tmpImg = g.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
//        BufferedImage resizedImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2d = resizedImg.createGraphics();
//        g2d.drawImage(tmpImg, 0, 0, null);
//        g2d.dispose();
        game.getPlayer().render(g, this);


//        g.drawImage(game.getPlayer().render(), )

        // render //////////////////////////////
        g.dispose();
        bs.show();
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
}
