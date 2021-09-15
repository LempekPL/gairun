package com.game.gairun.interfaces;

import java.awt.*;

public interface Entity {
    void tick();

    void render(Graphics g);

    Rectangle getBounds();

    double getX();

    void setX(double setX);

    double getY();

    void setY(double setY);

    double getHitboxWidth();

    void setHitboxWidth(double hitboxWidth);

    double getHitboxHeight();

    void setHitboxHeight(double hitboxHeight);
}