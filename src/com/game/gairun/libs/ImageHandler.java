package com.game.gairun.libs;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageHandler {
    private BufferedImage image;

    public ImageHandler(String textureName) {
        BufferedImageLoader loader = new BufferedImageLoader();
        try {
            image = loader.loadImage("/textures/%s.png".formatted(textureName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage grabImage(int col, int row, int width, int height) {
        return image.getSubimage(col * width, row * height, width, height);
    }
}
