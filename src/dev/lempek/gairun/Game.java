package dev.lempek.gairun;

import dev.lempek.gairun.controllers.KeyControl;
import dev.lempek.gairun.controllers.MapControl;
import dev.lempek.gairun.controllers.MouseControl;
import dev.lempek.gairun.instances.Window;
import com.gairun.instances.*;
import dev.lempek.gairun.instances.Camera;
import dev.lempek.gairun.instances.Console;
import dev.lempek.gairun.instances.GUIRenderer;
import dev.lempek.gairun.instances.Player;

import java.awt.*;
import java.awt.image.BufferStrategy;

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
    private boolean limitedFrames = true;
    private final float GRAVITATIONAL_PULL = 9.81f;
    // threading
    private boolean isRunning = false;
    private Thread thread;
    // instances
    private Player player;
    private Camera camera;
    private Console console;
    private MapControl mapControl;
    private KeyControl keyControl;
    private MouseControl mouseControl;
    private dev.lempek.gairun.instances.GUIRenderer GUIRenderer;

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
        keyControl = new KeyControl(this);
        addKeyListener(keyControl);
        mouseControl = new MouseControl(this);
        addMouseListener(mouseControl);
        player = new Player(0, 0, this);
        camera = new Camera(0, 0, this);
        console = new Console(this);
        mapControl = new MapControl(this);
        GUIRenderer = new GUIRenderer(this);
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
        // copy without g2d changes
        Graphics gStatic = g.create();
        // moving and scaling screen
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(WIDTH * (1 - camera.getScale()) / 2, HEIGHT * (1 - camera.getScale()) / 2);
        g2d.scale(camera.getScale(), camera.getScale());
        g2d.translate((int) -camera.getX(), (int) -camera.getY());

        // rendering
        player.render(g);
//        mapControl.render(g);

        // rendering gui stuff
        GUIRenderer.render(g, gStatic);

        // freeing data and displaying it
        g2d.dispose();
        gStatic.dispose();
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

    public double getFramerate() {
        return framerate;
    }

    public int getLastFrames() {
        return lastFrames;
    }

    public int getLastTicks() {
        return lastTicks;
    }

    public boolean isLimitedFrames() {
        return limitedFrames;
    }

    public Player getPlayer() {
        return player;
    }

    public Camera getCamera() {
        return camera;
    }

    public Console getConsole() {
        return console;
    }

    public MapControl getMapControl() {
        return mapControl;
    }

    public KeyControl getKeyControl() {
        return keyControl;
    }

    public MouseControl getMouseControl() {
        return mouseControl;
    }
}
