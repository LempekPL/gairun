package com.gairun.controllers;

import com.gairun.Game;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KeyInput extends KeyAdapter {
    private final Game game;
    private final Set<Integer> ignoreKeys = Set.of(KeyEvent.VK_SLASH, KeyEvent.VK_ENTER, KeyEvent.VK_TAB, KeyEvent.VK_UP, KeyEvent.VK_DOWN);
    List<Integer> keysPressed = new ArrayList<>();

    public KeyInput(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (!keysPressed.contains(key)) {
            keysPressed.add(key);
        }
        if (game.getConsole().isOpened()) {
            if (keysPressed.contains(KeyEvent.VK_CONTROL)) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_V -> {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        String tempString = game.getConsole().getCommand();
                        String copyFromClipboard = null;
                        try {
                            copyFromClipboard = (String) clipboard.getData(DataFlavor.stringFlavor);
                        } catch (UnsupportedFlavorException | IOException ex) {
                            // You can ignore errors. You probably used text that has invisible metadata that can't be converted to string");
                            ex.printStackTrace();
                        }
                        tempString += copyFromClipboard;
                        game.getConsole().setCommand(tempString.replaceAll("[^a-zA-Z0-9\s.-]", ""));
                        game.getConsole().setAlreadyUsed(true);
                    }
                    case KeyEvent.VK_X -> {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(new StringSelection(game.getConsole().getCommand()), null);
                        game.getConsole().setCommand("");
                    }
                    case KeyEvent.VK_C -> {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(new StringSelection(game.getConsole().getCommand()), null);
                    }
                    case KeyEvent.VK_BACK_SPACE -> {
                        game.getConsole().setCommand("");
                    }
                    case KeyEvent.VK_F -> {
                        game.getConsole().setCommand(game.getConsole().getHelpedCommand());
                        game.getConsole().setAlreadyUsed(true);
                    }
                }
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (game.getConsole().getCommand().length() > 0) {
                    game.getConsole().setCommand(game.getConsole().getCommand().substring(0, game.getConsole().getCommand().length() - 1));
                }
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
                game.getConsole().setCommand(game.getConsole().getHelpedCommand());
                game.getConsole().setAlreadyUsed(true);
            } else {
                if (!ignoreKeys.contains(e.getKeyCode())) {
                    String tempString = game.getConsole().getCommand() + e.getKeyChar();
                    game.getConsole().setCommand(tempString.replaceAll("[^a-zA-Z0-9\s.-]", ""));
                    game.getConsole().setAlreadyUsed(true);
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
