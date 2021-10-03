package com.gairun.interfaces;

import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Texture {
    private final BufferedImage image;
    private BufferedImage currentImage;
    private List<BufferedImage> imageFrames;
    private final JSONObject imageData;
    private boolean animated = false;
    private boolean paused = false;
    private List<Object> frames;
    private int index = 0;
    private int speed = 0;
    private int imageFrame = 0;

    public Texture(BufferedImage image, JSONObject imageData) {
        this.image = image;
        this.imageData = imageData;
        loadTexture();
    }

    public void runAnimation() {
        if (animated && !paused) {
            index++;
            if (index > speed) {
                index = 0;
                nextFrame();
            }
        }
    }

    private void nextFrame() {
        currentImage = imageFrames.get((int) frames.get(imageFrame));
        imageFrame++;
        if (imageFrame >= frames.size()) {
            imageFrame = 0;
        }
    }

    private void loadTexture() {
        imageFrames = new ArrayList<>();
        if (imageData.has("animation")) {
            JSONObject animationJSON = imageData.getJSONObject("animation");
            speed = animationJSON.getInt("speed");
            frames = animationJSON.getJSONArray("frameOrder").toList();
            for (int i = 0; i < animationJSON.getInt("frames"); i++) {
                imageFrames.add(image.getSubimage(0, i*imageData.getInt("height"), imageData.getInt("width"), imageData.getInt("height")));
            }
            animated = true;
        }
        currentImage = image.getSubimage(0, 0, imageData.getInt("width"), imageData.getInt("height"));
    }

    public BufferedImage getTexture() {
        return currentImage;
    }

    public boolean isAnimated() {
        return animated;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getFrame() {
        return imageFrame;
    }

    public void setFrame(int imageFrame) {
        this.imageFrame = imageFrame;
    }
}
