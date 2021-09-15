package com.game.gairun.libs;

import java.awt.image.BufferedImage;

public class SpriteSheet {

    private final BufferedImage image;

    public SpriteSheet(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage grabImage(int col, int row, int width, int height) {

        return image.getSubimage(col * width, row * height, width, height);

    }

}
