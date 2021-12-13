package dev.lempek.gairun.controllers;

import dev.lempek.gairun.Game;

import java.awt.event.MouseAdapter;

public class MouseControl extends MouseAdapter {
    private final Game game;

    public MouseControl(Game game) {
        this.game = game;
    }
}
