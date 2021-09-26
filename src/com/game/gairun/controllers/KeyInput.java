package com.game.gairun.controllers;

import com.game.gairun.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class KeyInput extends KeyAdapter {
    List<Integer> keysPressed = new ArrayList<>();
    private Game game;

    public KeyInput (Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (!keysPressed.contains(key)) {
            keysPressed.add(key);
        }
        if (game.isConsoleOpened()) {
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (game.getConsoleCommand().length() > 0) {
                    game.setConsoleCommand(game.getConsoleCommand().substring(0, game.getConsoleCommand().length() - 1));
                }
            } else {
                if (!(e.getKeyCode() == KeyEvent.VK_SLASH || e.getKeyCode() == KeyEvent.VK_ENTER)) {
                    game.setConsoleCommand(game.getConsoleCommand() + e.getKeyChar());
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        keysPressed.remove(Integer.valueOf(key));
    }

    public void removeKey(Integer key) {
        keysPressed.remove(key);
    }

    public boolean checkKey(Integer key) {
        if (keysPressed.contains(key)) {
            removeKey(key);
            return true;
        }
        return false;
    }

    public List<Integer> getKeysPressed() {
        return keysPressed;
    }
}
