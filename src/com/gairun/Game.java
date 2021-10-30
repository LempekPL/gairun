package com.gairun;

import com.gairun.controllers.KeyInput;
import com.gairun.controllers.MapController;
import com.gairun.interfaces.Blocks;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: custom screen size (settings > create file > restart game > load external file > set screen size)
// TODO: check why when player walks in diagonal (press W and D) and collides with blocks above and is "outside" rendered map
//  then he "glitches" and moves fast in left or when player is in blocks (only happenes when fly is enabled), tempfix: just make additional 2 rows of "-" at the bottom

public class Game extends Canvas implements Runnable {
    // game values
    public static final int WIDTH = 1200;
    // .../ 16 * 9; for 16:9 obviously
    // .../ 4 * 3; for 4:3
    // .../ 5 * 4; for 5:4
    // .../ 12 * 9 for 12:9
    public static final int HEIGHT = WIDTH / 12 * 9;
    public final String TITLE = "gairun";
    private final List<String> consoleHistory = new ArrayList<>();
    private double gameSpeed = 1;
    private double framerate = 60.0;
    private int lastFrames;
    private int lastTicks;
    private boolean limitedFrames = false;
    // threading
    private boolean isRunning = false;
    private Thread thread;
    // instances
    private Player p;
    private Camera cam;
    private MapController mapController;
    private KeyInput keyListener;
    // console
    private boolean consoleOpened;
    private String consoleCommand = "";

    public Game() {
        new Window(WIDTH, HEIGHT, TITLE, this);
        init();
        start();
    }

    public static void main(String[] args) {
        new Game();
    }

    public void init() {
        requestFocus();
        p = new Player(0, 0, this);
        cam = new Camera(0, 0, this);
        mapController = new MapController(this);
        keyListener = new KeyInput(this);
        addKeyListener(keyListener);
        mapController.loadMap("menu", "start");
    }

    public void run() {
        long lastTime = System.nanoTime();
        long lastTimeR = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        double deltaR = 0;
        int frames = 0;
        int ticks = 0;
        long timer = System.currentTimeMillis();

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (ns / gameSpeed);
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
                ticks++;
            }
            if (limitedFrames) {
                double nr = 1000000000 / framerate;
                long nowR = System.nanoTime();
                deltaR += (nowR - lastTimeR) / nr;
                lastTimeR = nowR;
                while (deltaR >= 1) {
                    render();
                    deltaR--;
                    frames++;
                }
            } else {
                render();
                frames++;
            }
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                lastFrames = frames;
                lastTicks = ticks;
                frames = 0;
                ticks = 0;
            }
        }
        stop();
    }

    private void tick() {
        cam.tick();
        p.tick();
        mapController.tick();
        if (keyListener.checkKey(KeyEvent.VK_SLASH)) {
            consoleOpened = !consoleOpened;
        }
        if (consoleOpened && keyListener.checkKey(KeyEvent.VK_ENTER) && consoleCommand.length() > 0) {
            String[] commandString = consoleCommand.split(" ");
            switch (commandString[0]) {
                case "loadmap" -> {
                    consoleHistory.add(consoleCommand);
                    if (commandString[1] == null || commandString[2] == null) {
                        System.out.println("SOMETHING IS MISSING");
                    } else {
                        mapController.loadMap(commandString[1], commandString[2]);
                    }
                }
                case "fly" -> {
                    consoleHistory.add(consoleCommand);
                    p.setFlying(!p.isFlying());
                }
                case "debug" -> {
                    consoleHistory.add(consoleCommand);
                    cam.setDebug(!cam.isDebug());
                }
                case "teleport", "tp" -> {
                    consoleHistory.add(consoleCommand);
                    float toSpawnX = (int) Float.parseFloat(commandString[1]);
                    if (Float.parseFloat(commandString[1]) != toSpawnX) {
                        toSpawnX = Float.parseFloat(commandString[1]) - 0.5F;
                    }
                    p.spawnPlayer(toSpawnX * 16, Float.parseFloat(commandString[2]) * 16);
                }
                case "gamespeed" -> {
                    consoleHistory.add(consoleCommand);
                    gameSpeed = Double.parseDouble(commandString[1]);
                    if (gameSpeed < 0) {
                        gameSpeed = 1;
                    }
                }
                case "noclip" -> {
                    consoleHistory.add(consoleCommand);
                    p.setNoclip(!p.isNoclip());
                }
                case "invisible" -> {
                    consoleHistory.add(consoleCommand);
                    p.setInvisible(!p.isInvisible());
                }
                case "framerate" -> {
                    consoleHistory.add(consoleCommand);
                    double parsedFramerate = Double.parseDouble(commandString[1]);
                    if (parsedFramerate > 0 && parsedFramerate <= 500) {
                        framerate = parsedFramerate;
                        limitedFrames = true;
                    } else {
                        limitedFrames = false;
                    }
                }
                case "scale" -> {
                    consoleHistory.add(consoleCommand);
                    if (Objects.equals(commandString[1], "off")) {
                        cam.setScale(2);
                        cam.setScaling(true);
                    } else if (Objects.equals(commandString[1], "set")) {
                        float parsedScale = Float.parseFloat(commandString[2]);
                        cam.setScale(parsedScale);
                        cam.setScaling(false);
                    }
                }
            }
            consoleCommand = "";
            consoleOpened = false;
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        // clearing screen
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // creating copies
        Graphics gCopy = g.create();
        // moving and scaling screen
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(WIDTH * (1 - cam.getScale()) / 2, HEIGHT * (1 - cam.getScale()) / 2);
        g2d.scale(cam.getScale(), cam.getScale());
        g2d.translate((int) -cam.getX(), (int) cam.getY());
        // rendering
        p.render(g);
        mapController.render(g);
        g.setColor(Color.green);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 5));
        for (List<Blocks> blockList : mapController.getMapBlocks()) {
            for (Blocks block : blockList) {
                g.drawString("x: %s".formatted((int) block.getX()), (int) block.getX() + Game.WIDTH/2, (int) -block.getY() + Game.HEIGHT / 2 + 5);
                g.drawString("y: %s".formatted((int) block.getY()), (int) block.getX() + Game.WIDTH/2, (int) -block.getY() + Game.HEIGHT / 2 + 10);
            }
        }
        // console
        if (consoleOpened) {
            gCopy.setColor(new Color(50, 50, 50, 127));
            gCopy.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            gCopy.setColor(Color.white);
            gCopy.drawString(">" + consoleCommand, 40, 40);
            for (int i = 0; i < consoleHistory.size(); i++) {
                gCopy.drawString(consoleHistory.get(i), 50, (consoleHistory.size() - i) * 11 + 50);
            }
        }
        // debug camera
        if (cam.isDebug()) {
            // fps and ticks counter
            gCopy.setColor(Color.green);
            gCopy.drawString(lastFrames + "FPS, " + lastTicks + " ticks", 0, 10);
            // camera move to limit
            g.setColor(Color.yellow);
            float xRender = cam.getX() + (float) Game.WIDTH / 2 - cam.getCameraMovementLimit();
            float yRender = -cam.getY() + (float) Game.HEIGHT / 2 - cam.getCameraMovementLimit();
            g.drawRect((int) xRender, (int) yRender, cam.getCameraMovementLimit() * 2, cam.getCameraMovementLimit() * 2);
            gCopy.setColor(Color.white);
            gCopy.drawString("XY: %s, %s".formatted((float) Math.round(p.getX() / 16 * 1000) / 1000, (float) Math.round(p.getY() / 16 * 1000) / 1000), 5, 25);
            gCopy.drawString("XY: %s, %s".formatted(p.getX(), p.getY()), 5, 40);
        } else {
            // fps counter
            gCopy.setColor(Color.green);
            gCopy.drawString(lastFrames + "FPS", 0, 10);
        }
        // freeing data and displaying it
        g2d.dispose();
        gCopy.dispose();
        g.dispose();
        bs.show();
    }

    private synchronized void start() {
        if (isRunning) return;
        thread = new Thread(this);
        thread.start();
        isRunning = true;
    }

    private synchronized void stop() {
        if (!isRunning) return;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isRunning = false;
    }

    public Player getPlayer() {
        return p;
    }

    public Camera getCamera() {
        return cam;
    }

    public MapController getMapController() {
        return mapController;
    }

    public KeyInput getKeyListener() {
        return keyListener;
    }

    public int getLastFrames() {
        return lastFrames;
    }

    public boolean isConsoleOpened() {
        return consoleOpened;
    }

    public String getTITLE() {
        return TITLE;
    }

    public String getConsoleCommand() {
        return consoleCommand;
    }

    public void setConsoleCommand(String consoleCommand) {
        this.consoleCommand = consoleCommand;
    }

    public double getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(double gameSpeed) {
        this.gameSpeed = gameSpeed;
    }
}
