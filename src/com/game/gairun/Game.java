package com.game.gairun;

import com.game.gairun.libs.ImageHandler;
import com.game.gairun.libs.KeyInput;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.Serial;

// TODO: custom width (settings and external file)
// TODO: coordinate system
// TODO: collision system
// TODO: map system

public class Game extends Canvas implements Runnable {
    @Serial
    private static final long serialVersionUID = 1L;
    // game static values
    public static final int WIDTH = 1200;
    // .../ 16 * 9; for 16:9 obviously
    // .../ 4 * 3; for 4:3
    // .../ 5 * 4; for 5:4
    // .../ 12 * 9 for 12:9
    public static final int HEIGHT = WIDTH / 12 * 9;
    public final String TITLE = "gairun";
    // threading
    private boolean running = false;
    private Thread thread;

    private Player p;
    private Camera cam;

    public void init() {
        ImageHandler ih = new ImageHandler("gairun1");

        p = new Player(100, 100, ih.grabImage(0,0,16,32));

        requestFocus();
        addKeyListener(new KeyInput(this));

        cam = new Camera(WIDTH, HEIGHT);
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
            cam.render(this);
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(updates + " Ticks, FPS " + frames);
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }

    private void tick() {
        p.tick();
    }

    private void render() {
    }

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

}
