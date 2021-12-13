package dev.lempek.gairun.interfaces;

import dev.lempek.gairun.Game;

import java.awt.*;

public interface GameEntity {
    void tick();
    void render(Graphics g);

    Rectangle getBounds();

    float getX();
    void setX(float setX);
    float getY();
    void setY(float setY);
}