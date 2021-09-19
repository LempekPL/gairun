package com.game.gairun;

import com.game.gairun.libs.ImageHandler;
import com.game.gairun.controllers.KeyInput;
import com.game.gairun.controllers.MapController;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

// TODO: custom screen size (settings > create file > restart game > load external file > set screen size)
// TODO: collision

public class Game extends Canvas implements Runnable {
    // game static values
    public static final int WIDTH = 1200;
    // .../ 16 * 9; for 16:9 obviously
    // .../ 4 * 3; for 4:3
    // .../ 5 * 4; for 5:4
    // .../ 12 * 9 for 12:9
    public static final int HEIGHT = WIDTH / 12 * 9;
    public final String TITLE = "gairun";
    // threading
    private boolean isRunning = false;
    private Thread thread;

    // instances
    private Player p;
    private Camera cam;
    private MapController mapController;
    private KeyInput keyListener;
    BufferedImage screen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    // frame rate
    private int lastFrames;

    public Game() {
        new Window(WIDTH, HEIGHT, TITLE, this);
        start();
        init();
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
        keyListener = new KeyInput();
        addKeyListener(keyListener);
    }

    public void run() {
        init();
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
        if (keyListener.checkKey(KeyEvent.VK_1)) {
            mapController.loadMap("main", "1");
        } else if (keyListener.checkKey(KeyEvent.VK_2)) {
            mapController.loadMap("main", "2");
        } else if (keyListener.checkKey(KeyEvent.VK_3)) {
            mapController.loadMap("side", "3");
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
        bsGraphics.clearRect(0, 0, WIDTH, HEIGHT);
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
        // fps counter
        bsGraphics.setColor(Color.green);
        bsGraphics.drawString(lastFrames+"FPS",0,10);
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
}
