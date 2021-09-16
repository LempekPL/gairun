package com.game.gairun.libs;

import com.game.gairun.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    Game game;

    public KeyInput(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_D) {
            game.getPlayer().setFriction(0);
            game.getPlayer().setVelX(5);
            game.getPlayer().setSideMultiplier(1);
        }
        if (key == KeyEvent.VK_A) {
            game.getPlayer().setFriction(0);
            game.getPlayer().setVelX(5);
            game.getPlayer().setSideMultiplier(-1);
        }
        if ((key == KeyEvent.VK_W || key == KeyEvent.VK_SPACE) && game.getPlayer().getJumps() > 0) {
            game.getPlayer().setVelY(5);
            game.getPlayer().setJumps(game.getPlayer().getJumps() - 1);
        }

    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_D) {
            if (game.getPlayer().isOnSurface()) {
                game.getPlayer().setFriction(0.2);
            } else {
                game.getPlayer().setFriction(0.1);
            }
        }
        if (key == KeyEvent.VK_A) {
            if (game.getPlayer().isOnSurface()) {
                game.getPlayer().setFriction(0.2);
            } else {
                game.getPlayer().setFriction(0.1);
            }
        }
    }

}
