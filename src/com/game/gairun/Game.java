package com.game.gairun;

import com.game.gairun.controllers.KeyInput;
import com.game.gairun.controllers.MapController;
import com.game.gairun.libs.ImageHandler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: custom screen size (settings > create file > restart game > load external file > set screen size)
// TODO: rendering useing Graphics2D's transform and scale

public class Game extends Canvas implements Runnable {
    // game static values
    public static final int WIDTH = 1200;
    // .../ 16 * 9; for 16:9 obviously
    // .../ 4 * 3; for 4:3
    // .../ 5 * 4; for 5:4
    // .../ 12 * 9 for 12:9
    public static final int HEIGHT = WIDTH / 12 * 9;
    public final String TITLE = "gairun";
    private final List<String> consoleHistory = new ArrayList<>();
    BufferedImage screen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    // threading
    private boolean isRunning = false;
    private Thread thread;
    // instances
    private Player p;
    private Camera cam;
    private MapController mapController;
    private KeyInput keyListener;
    // frame rate
    private int lastFrames;
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
        // find otherplace :/
        ImageHandler ih = new ImageHandler("gairun1");
        p = new Player(0, 0, ih.grabImage(0, 0, 16, 32), this);
        cam = new Camera(0, 0, this);
        mapController = new MapController(this);
        keyListener = new KeyInput(this);
        addKeyListener(keyListener);
        mapController.loadMap("menu", "start");
    }

    public void run() {
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick();
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
//                System.out.println(updates + " Ticks, FPS " + frames);
                lastFrames = frames;
                frames = 0;
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
            }
            consoleCommand = "";
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics bsGraphics = bs.getDrawGraphics();
        // clearing screen
        bsGraphics.setColor(Color.black);
        bsGraphics.fillRect(0, 0, WIDTH, HEIGHT);
        Graphics g = screen.createGraphics();
        g.clearRect(0, 0, WIDTH, HEIGHT);
        // rendering
        p.render(g);
        mapController.render(g);
        // debug camera
        if (cam.isDebug()) {
            // camera move to limit
            g.setColor(Color.yellow);
            g.drawRect(WIDTH / 2 - cam.getCameraMovementLimit(), HEIGHT / 2 - cam.getCameraMovementLimit(), cam.getCameraMovementLimit() * 2, cam.getCameraMovementLimit() * 2);
        }
        // draw virtual image to camera
        bsGraphics.drawImage(screen, (int) -(WIDTH * (cam.getScale() - 1)), (int) -(HEIGHT * (cam.getScale() - 1)), (int) (WIDTH * cam.getScale()), (int) (HEIGHT * cam.getScale()), 0, 0, WIDTH, HEIGHT, null);
//        bsGraphics.drawImage(screen, 0, 0, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, null);
        // fps counter
        bsGraphics.setColor(Color.green);
        bsGraphics.drawString(lastFrames + "FPS", 0, 10);
        // console
        if (consoleOpened) {
//            g.dispose();
//            Graphics console = screen.createGraphics();
            bsGraphics.setColor(Color.gray);
            bsGraphics.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            bsGraphics.setColor(Color.black);
            bsGraphics.drawString(">"+consoleCommand, 100, 90);
            for (int i = 0; i < consoleHistory.size(); i++) {
                bsGraphics.drawString(consoleHistory.get(i), 110, i*11+100);
            }
//            console.drawImage(screen, -WIDTH, -HEIGHT, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, null);
        }
        // freeing data and displaying it
        g.dispose();
        bsGraphics.dispose();
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
}
