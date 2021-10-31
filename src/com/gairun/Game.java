package com.gairun;

import com.gairun.controllers.BackgroundController;
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
// TODO: check why when player walks in diagonal (press W and D) and collides with data above and is "outside" rendered map
//  then he "glitches" and moves fast in left or when player is in data (only happenes when fly is enabled), tempfix: just make additional 2 rows of "-" at the bottom

public class Game extends Canvas implements Runnable {
    // game values
    public static final int WIDTH = 1200;
    // .../ 16 * 9; for 16:9 obviously
    // .../ 4 * 3; for 4:3
    // .../ 5 * 4; for 5:4
    // .../ 12 * 9 for 12:9
    public static final int HEIGHT = WIDTH / 12 * 9;
    public final String TITLE = "gairun";
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
    private BackgroundController bgController;
    private KeyInput keyListener;
    private Console console;

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
        bgController = new BackgroundController(this);
        console = new Console(this);
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
        bgController.tick();
        console.tick();
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
        bgController.render(g);
        p.render(g);
        mapController.render(g);
        console.render(gCopy);

        // debug camera
        gCopy.setFont(new Font("Default", Font.PLAIN, 12));
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
            gCopy.drawString("px XY: %s, %s".formatted(p.getX(), p.getY()), 5, 40);
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

    public Console getConsole() {
        return console;
    }

    public MapController getMapController() {
        return mapController;
    }

    public BackgroundController getBackgroundController() {
        return bgController;
    }

    public KeyInput getKeyListener() {
        return keyListener;
    }

    public int getLastFrames() {
        return lastFrames;
    }

    public String getTITLE() {
        return TITLE;
    }

    public double getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(double gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    public double getFramerate() {
        return framerate;
    }

    public void setFramerate(double framerate) {
        this.framerate = framerate;
    }

    public boolean isLimitedFrames() {
        return limitedFrames;
    }

    public void setLimitedFrames(boolean limitedFrames) {
        this.limitedFrames = limitedFrames;
    }
}
