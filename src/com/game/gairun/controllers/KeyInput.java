package com.game.gairun.controllers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class KeyInput extends KeyAdapter {
    List<Integer> keysPressed = new ArrayList<>();

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (!keysPressed.contains(key)) {
            keysPressed.add(key);
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
