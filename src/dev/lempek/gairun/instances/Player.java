package dev.lempek.gairun.instances;

import dev.lempek.gairun.Game;
import dev.lempek.gairun.templates.GameTemplateEntity;

import java.awt.*;

public class Player extends GameTemplateEntity {
    private Game game;

    public Player(Game game, float x, float y, float mass) {
        super(game, x, y, mass);
    }

    public void tick() {

    }
}
