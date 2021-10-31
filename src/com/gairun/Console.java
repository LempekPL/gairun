package com.gairun;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Console {
    private final Game game;
    private final String[] commands = new String[]{
            "teleport", "fly", "noclip",
            "invisible", "loadmap", "gamespeed",
            "framerate", "debug", "scale",
            "teleportCamera", "clear", "help",
            "h", "tp", "map"
    };
    private List<String> commandLine = new ArrayList<>();
    private List<String> commandHistory = new ArrayList<>();
    private int lastSelected = 1;
    private boolean alreadyUsed = false;

    private boolean consoleOpened;
    private String consoleCommand = "";

    private List<String> consoleCommandHelp;
    private int consoleCommandHelpBoxSize;
    private int consoleCommandSelected;
    private boolean consoleCommandForceHelp = false;

    private String blinker;
    private int blinkerTimer;

    public Console(Game game) {
        this.game = game;
    }

    public void tick() {
        if (game.getKeyListener().checkKey(KeyEvent.VK_SLASH)) {
            consoleOpened = !consoleOpened;
            consoleCommand = "";
        }
        if (game.getKeyListener().checkKey(KeyEvent.VK_BACK_QUOTE)) {
            consoleCommandForceHelp = true;
        }
        if (game.getKeyListener().checkKey(KeyEvent.VK_BACK_SPACE) || !consoleOpened) {
            consoleCommandForceHelp = false;
        }
        if (!consoleCommandForceHelp && !alreadyUsed && commandHistory.size() > 0) {
            if (game.getKeyListener().checkKey(KeyEvent.VK_UP)) {
                lastSelected++;
                lastSelected = clamp(lastSelected, 1, commandHistory.size());
                consoleCommand = commandHistory.get(commandHistory.size() - lastSelected);
            } else if (game.getKeyListener().checkKey(KeyEvent.VK_DOWN)) {
                lastSelected--;
                lastSelected = clamp(lastSelected, 1, commandHistory.size());
                consoleCommand = commandHistory.get(commandHistory.size() - lastSelected);
            }
        }
        if (commandHistory.size() > 100) {
            commandHistory.remove(0);
        }
        if (Objects.equals(consoleCommand, "")) {
            alreadyUsed = false;
        }
        if (consoleOpened && game.getKeyListener().checkKey(KeyEvent.VK_ENTER) && consoleCommand.length() > 0) {
            String[] commandString = consoleCommand.split(" ");
            commandLine.add(consoleCommand);
            commandHistory.add(consoleCommand);
            lastSelected = 0;
            if (commandString.length > 0) {
                switch (commandString[0]) {
                    case "help", "h" -> commandHelp(commandString);
                    case "fly" -> {
                        if (commandString.length >= 2) {
                            if (Objects.equals(commandString[1], "true")) game.getPlayer().setFlying(true);
                            else if (Objects.equals(commandString[1], "false")) game.getPlayer().setFlying(false);
                        } else {
                            game.getPlayer().setFlying(!game.getPlayer().isFlying());
                        }
                    }
                    case "clear" -> commandLine = new ArrayList<>();
                    case "loadmap", "map" -> {
                        List<String> sets = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File("res/maps").list())));
                        List<String> ids = new ArrayList<>();
                        if (commandString.length >= 2 && sets.contains(commandString[1])) {
                            ids = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File("res/maps/%s".formatted(commandString[1])).list())));
                            ids.removeIf(value -> value.endsWith(".csv"));
                            ids.replaceAll(e -> e.replaceAll(".json", ""));
                        }
                        if (commandString.length <= 1 || (sets.size() > 0 && !sets.contains(commandString[1]))) {
                            commandLine.add("&R Can't find this map set");
                            commandLine.add("&O Available map sets are:");
                            commandLine.add("&O     %s".formatted(sets));
                        } else if (commandString.length <= 2 || (ids.size() > 0 && !ids.contains(commandString[2]))) {
                            commandLine.add("&R Can't find this map id");
                            commandLine.add("&O Available ids from this set are:");
                            commandLine.add("&O     %s".formatted(ids));
                        } else {
                            game.getMapController().loadMap(commandString[1], commandString[2]);
                        }
                    }
                    case "teleport", "tp" -> {
                        float toSpawnX = (int) Float.parseFloat(commandString[1]);
                        if (Float.parseFloat(commandString[1]) != toSpawnX) {
                            toSpawnX = Float.parseFloat(commandString[1]) - 0.5F;
                        }
                        game.getPlayer().spawnPlayer(toSpawnX * 16, Float.parseFloat(commandString[2]) * 16);
                    }
                    case "noclip" -> {
                        if (commandString.length >= 2) {
                            if (Objects.equals(commandString[1], "true")) game.getPlayer().setNoclip(true);
                            else if (Objects.equals(commandString[1], "false")) game.getPlayer().setNoclip(false);
                        } else {
                            game.getPlayer().setNoclip(!game.getPlayer().isNoclip());
                        }
                    }
                    case "invisible" -> {
                        if (commandString.length >= 2) {
                            if (Objects.equals(commandString[1], "true")) game.getPlayer().setInvisible(true);
                            else if (Objects.equals(commandString[1], "false")) game.getPlayer().setInvisible(false);
                        } else {
                            game.getPlayer().setInvisible(!game.getPlayer().isInvisible());
                        }
                    }
                    case "gamespeed" -> {
                        if (commandString.length >= 2) {
                            if (Objects.equals(commandString[1], "set") && commandString.length >= 3) {
                                double old = game.getGameSpeed();
                                game.setGameSpeed(Double.parseDouble(commandString[2]));
                                if (game.getGameSpeed() < 0) {
                                    game.setGameSpeed(old);
                                }
                            } else if (Objects.equals(commandString[1], "default")) {
                                game.setGameSpeed(1);
                            } else {
                                commandLine.add("&Q Current game speed: %s".formatted(game.getGameSpeed()));
                            }
                        } else {
                            commandLine.add("&Q Current game speed: %s".formatted(game.getGameSpeed()));
                        }
                    }
                    case "framerate" -> {
                        if (commandString.length >= 2) {
                            if (Objects.equals(commandString[1], "set") && commandString.length >= 3) {
                                double old = game.getFramerate();
                                game.setFramerate(Double.parseDouble(commandString[1]));
                                if (game.getFramerate() < 0 || game.getFramerate() > 500) {
                                    game.setGameSpeed(old);
                                }
                                game.setLimitedFrames(true);
                            } else if (Objects.equals(commandString[1], "off")) {
                                game.setLimitedFrames(false);
                            } else if (Objects.equals(commandString[1], "default")) {
                                game.setGameSpeed(60);
                                game.setLimitedFrames(true);
                            } else {
                                commandLine.add("&Q Current frame limit: %s".formatted(game.getGameSpeed()));
                            }
                        } else {
                            commandLine.add("&Q Current frame limit: %s".formatted(game.getGameSpeed()));
                        }
                    }
                    case "debug" -> {
                        if (commandString.length >= 2) {
                            if (Objects.equals(commandString[1], "true")) game.getCamera().setDebug(true);
                            else if (Objects.equals(commandString[1], "false")) game.getCamera().setDebug(false);
                        } else {
                            game.getCamera().setDebug(!game.getCamera().isDebug());
                        }
                    }
                    case "scale" -> {
                        if (commandString.length >= 2) {
                            if (Objects.equals(commandString[1], "set") && commandString.length >= 3) {
                                float parsedScale = Float.parseFloat(commandString[2]);
                                game.getCamera().setScale(parsedScale);
                                game.getCamera().setScaling(false);
                            } else if (Objects.equals(commandString[1], "default")) {
                                game.getCamera().setScale(2);
                                game.getCamera().setScaling(true);
                            } else {
                                commandLine.add("&Q Current camera scale: %s".formatted(game.getCamera().getScale()));
                            }
                        } else {
                            commandLine.add("&Q Current camera scale: %s".formatted(game.getCamera().getScale()));
                        }
                    }
                    default -> {
                        commandLine.add("&R Error: \"%s\" is not a command".formatted(commandString[0]));
                        commandLine.add("&R Use 'help' to find out what commands are available");
                    }
                }
                consoleCommand = "";
            }
            consoleCommandForceHelp = false;
        }
        blinkerTimer++;
        if (blinkerTimer > 30) {
            if (Objects.equals(blinker, "")) {
                blinker = "_";
            } else {
                blinker = "";
            }
            blinkerTimer = 0;
        }
        consoleCommandHelp = new ArrayList<>();
        consoleCommandHelpBoxSize = 0;
        for (String command : commands) {
            if (!(Objects.equals(consoleCommand, "") && !consoleCommandForceHelp) && command.contains(consoleCommand)) {
                consoleCommandHelp.add(command);
                if (consoleCommandHelpBoxSize < command.length()) {
                    consoleCommandHelpBoxSize = command.length();
                }
            }
        }
        if (game.getKeyListener().checkKey(KeyEvent.VK_DOWN)) {
            consoleCommandSelected--;
        } else if (game.getKeyListener().checkKey(KeyEvent.VK_UP)) {
            consoleCommandSelected++;
        }
        consoleCommandSelected = clamp(consoleCommandSelected, 0, consoleCommandHelp.size() - 1);
    }

    public void render(Graphics g) {
        if (!consoleOpened) return;
        g.setFont(new Font("Default", Font.PLAIN, 18));
        g.setColor(new Color(50, 50, 50, 127));
        g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
        g.setColor(new Color(80, 80, 80, 100));
        g.fillRect(20, Game.HEIGHT - 68, Game.WIDTH - 40, 25);
        g.setColor(Color.white);
        g.drawString(consoleCommand + blinker, 22, Game.HEIGHT - 50);
        for (int i = 0; i < commandLine.size(); i++) {
            if ((commandLine.size() - i) > 60) commandLine.remove(0);
            String renderText = commandLine.get(i);
            if (renderText.startsWith("&")) {
                switch (renderText.charAt(1)) {
                    case 'R' -> g.setColor(Color.red);
                    case 'G' -> g.setColor(Color.green);
                    case 'B' -> g.setColor(Color.blue);
                    case 'K' -> g.setColor(Color.black);
                    case 'Q' -> g.setColor(Color.gray);
                    case 'L' -> g.setColor(Color.lightGray);
                    case 'C' -> g.setColor(Color.cyan);
                    case 'M' -> g.setColor(Color.magenta);
                    case 'Y' -> g.setColor(Color.yellow);
                    case 'O' -> g.setColor(Color.orange);
                }
                renderText = renderText.substring(2);
            }
            g.drawString(renderText, 22, (Game.HEIGHT - 68 - (commandLine.size() - i) * 18));
            g.setColor(Color.WHITE);
        }
        for (int i = 0; i < consoleCommandHelp.size(); i++) {
            Color bgColor = new Color(50, 50, 50, 255);
            Color txtColor = new Color(170, 170, 170, 255);
            if (i == consoleCommandHelp.size() - 1 - consoleCommandSelected) {
                bgColor = new Color(80, 80, 80, 255);
                txtColor = Color.WHITE;
            }
            g.setColor(bgColor);
            g.fillRect(50, Game.HEIGHT - 72 - (consoleCommandHelp.size() - i) * 25, 20 * consoleCommandHelpBoxSize, 25);
            g.setColor(txtColor);
            g.drawString(consoleCommandHelp.get(i), 55, (Game.HEIGHT - 80 - (consoleCommandHelp.size() - i - 1) * 25));
        }
    }

    public boolean isOpened() {
        return consoleOpened;
    }

    public String getCommand() {
        return consoleCommand;
    }

    public void setCommand(String consoleCommand) {
        this.consoleCommand = consoleCommand;
    }

    public String getHelpedCommand() {
        if (consoleCommandHelp.size() <= 0) {
            return "";
        }
        return consoleCommandHelp.get(consoleCommandHelp.size() - consoleCommandSelected - 1);
    }

    public void setAlreadyUsed(boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }

    private void commandHelp(String[] commandString) {
        if (commandString.length == 1 || commandString[1] == null) {
            commandLine.add("&Q Use \"help <command>\" to get help about command");
            commandLine.add("&Q Avaiable commands:");
            commandLine.add("&Q     teleport, fly, noclip, invisible, (WIP) playerMaxSpeed,");
            commandLine.add("&Q     map, gamespeed, framerate,");
            commandLine.add("&Q     debug, scale, (WIP) teleportCamera, (WIP) stopCamera, (WIP) cameraFollowSpeed");
            commandLine.add("&Q     clear");
        } else {
            switch (commandString[1]) {
                case "help", "h" -> {
                    commandLine.add("&Q Shows possible commands or shows info about a command");
                    commandLine.add("&Q Example: 'help' or 'help <string command>'");
                    commandLine.add("&Q Alias: teleport, tp");
                }
                case "teleport", "tp" -> {
                    commandLine.add("&Q Teleports player to x and y position");
                    commandLine.add("&Q Example: 'teleport <float x> <float y>'");
                    commandLine.add("&Q Alias: teleport, tp");
                }
                case "fly" -> {
                    commandLine.add("&Q Enables or disables flying");
                    commandLine.add("&Q Example: 'fly' or 'fly <boolean state>'");
                }
                case "noclip" -> {
                    commandLine.add("&Q Turns on/off block collision");
                    commandLine.add("&Q Example: 'noclip' or 'noclip <boolean state>'");
                }
                case "invisible" -> {
                    commandLine.add("&Q Makes player invisible");
                    commandLine.add("&Q Example: 'invisible' or 'invisible <boolean state>'");
                }
                case "loadmap", "map" -> {
                    commandLine.add("&Q Changes map");
                    commandLine.add("&Q Example: 'map <string map set name> <string map id>'");
                    commandLine.add("&Q Alias: map, loadmap");
                }
                case "gamespeed" -> {
                    commandLine.add("&Q Changes game speed (60 ticks * gamespeed)");
                    commandLine.add("&Q Example: 'gamespeed set <double speed>' or 'gamespeed default'");
                }
                case "framerate" -> {
                    commandLine.add("&Q Limits frames per second");
                    commandLine.add("&Q Example: 'framerate set <int frames>' or 'framerate default' or 'framerate off'");
                }
                case "debug" -> {
                    commandLine.add("&Q Turns on/off debug view");
                    commandLine.add("&Q Example: 'debug' or 'debug <boolean state>'");
                }
                case "scale" -> {
                    commandLine.add("&Q Sets fixed camera scale");
                    commandLine.add("&Q Example: 'scale set <float scale>' or 'gamespeed default'");
                }
                case "clear" -> {
                    commandLine.add("&Q Clears command line");
                    commandLine.add("&Q Example: 'clear'");
                }
            }
        }
    }

    private int clamp(int value, int min, int max) {
        if (value >= max) return max;
        else if (value <= min) return min;
        return value;
    }
}
