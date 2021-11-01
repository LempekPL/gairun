package com.gairun.interfaces;

import com.gairun.Game;

public class GameValues {
    private Game game;

    public GameValues(Game game) {
        this.game = game;
    }

    public float getPlayerX() {
        return game.getPlayer().getX();
    }

    public void setPlayerX(float x) {
        game.getPlayer().spawnPlayer(x, game.getPlayer().getY());
    }

    public float getPlayerY() {
        return game.getPlayer().getY();
    }

    public void setPlayerY(float y) {
        game.getPlayer().spawnPlayer(game.getPlayer().getX(), y);
    }

    public void setPlayerTo(float x, float y) {
        game.getPlayer().spawnPlayer(x, y);
    }

    public float getPlayerAcceleration() {
        return game.getPlayer().getAcc();
    }

    public float getPlayerDeceleration() {
        return game.getPlayer().getDcc();
    }

    public void setPlayerAcceleration(float acc) {
        game.getPlayer().setAcc(acc);
    }

    public void setPlayerDeceleration(float dcc) {
        game.getPlayer().setDcc(dcc);
    }

    public boolean isPlayerFlying() {
        return game.getPlayer().isFlying();
    }

    public void setPlayerFlying(boolean flying) {
        game.getPlayer().setFlying(flying);
    }

    public boolean isPlayerInvisible() {
        return game.getPlayer().isInvisible();
    }

    public void setPlayerInvisible(boolean invisible) {
        game.getPlayer().setInvisible(invisible);
    }

    public boolean isPlayerBlockedKeys() {
        return game.getPlayer().isBlockedKeys();
    }

    public void setPlayerBlockedKeys(boolean blockedKeys) {
        game.getPlayer().setInvisible(blockedKeys);
    }

    public boolean isPlayerNoclip() {
        return game.getPlayer().isNoclip();
    }

    public void setPlayerNoclip(boolean noclip) {
        game.getPlayer().setInvisible(noclip);
    }
}
