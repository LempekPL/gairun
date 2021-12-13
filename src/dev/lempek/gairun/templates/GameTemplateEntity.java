package dev.lempek.gairun.templates;

import dev.lempek.gairun.Game;
import dev.lempek.gairun.interfaces.GameEntity;

public abstract class GameTemplateEntity implements GameEntity {
    private final Game game;
    private float x;
    private float y;
    private float velX;
    private float velY;
    private float acc;
    private float dcc;
    private float mass;

    public GameTemplateEntity(Game game, float x, float y, float mass) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.mass = mass;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}
