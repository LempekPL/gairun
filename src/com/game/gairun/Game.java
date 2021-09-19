package com.game.gairun;

import com.game.gairun.libs.ImageHandler;
import com.game.gairun.libs.KeyInput;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

// TODO: custom screen size (settings > create file > restart game > load external file > set screen size)

public class Game extends Canvas implements Runnable {
    // game static values
    public static final int WIDTH = 1200;
    // .../ 16 * 9; for 16:9 obviously
    // .../ 4 * 3; for 4:3
    // .../ 5 * 4; for 5:4
    // .../ 12 * 9 for 12:9
    public static final int HEIGHT = WIDTH / 12 * 9;
    @Serial
    private static final long serialVersionUID = 1L;
    public final String TITLE = "gairun";
    // threading
    private boolean running = false;
    private Thread thread;

    private Player p;
    private MapController mapCon;
    private Camera cam;
    private int lastFrames;

    public static void main(String[] args) {
        Game game = new Game();

        game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        game.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        game.setMinimumSize(new Dimension(WIDTH, HEIGHT));

        JFrame frame = new JFrame(game.TITLE);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.start();
    }

    public void init() {
        requestFocus();

        ImageHandler ih = new ImageHandler("gairun1");
        p = new Player(0, 0, ih.grabImage(0, 0, 16, 32), this);


        cam = new Camera(WIDTH, HEIGHT, this);

        mapCon = new MapController(this);
        addKeyListener(new KeyInput(this));
    }

    public void run() {
        init();
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick();
                updates++;
                delta--;
            }
            cam.render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
//                System.out.println(updates + " Ticks, FPS " + frames);
                lastFrames = frames;
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }

    private void tick() {
        p.tick();
        cam.tick();
    }

    private synchronized void start() {
        if (running)
            return;

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private synchronized void stop() {
        if (!running)
            return;

        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    public Player getPlayer() {
        return p;
    }

    public Camera getCamera() {
        return cam;
    }

    public MapController getMapController() {
        return mapCon;
    }

    public int getLastFrames() {
        return lastFrames;
    }
}
