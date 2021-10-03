package com.gairun.controllers;

import com.gairun.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KeyInput extends KeyAdapter {
    List<Integer> keysPressed = new ArrayList<>();
    private final Game game;
    private final Set<Integer> ignoreKeys = Set.of(KeyEvent.VK_SLASH, KeyEvent.VK_ENTER);

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
                if (!ignoreKeys.contains(e.getKeyCode())) {
                    String tempString = game.getConsoleCommand() + e.getKeyChar();
                    game.setConsoleCommand(tempString.replaceAll("[^a-zA-Z0-9\s.-]", ""));
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
