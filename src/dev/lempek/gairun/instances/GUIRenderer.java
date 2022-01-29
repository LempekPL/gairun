package dev.lempek.gairun.instances;

import dev.lempek.gairun.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GUIRenderer {
    private final Game game;

    public GUIRenderer(Game game) {
        this.game = game;
    }

    public void render(Graphics g, Graphics gStatic) {
        gStatic.setFont(new Font("Default", Font.PLAIN, 12));
//        if (game.getCamera().isDebug()) {
//            // player hitbox debug
//            g.setColor(Color.red);
//            Rectangle2D mainHitbox = game.getPlayer().getBounds();
//            g.drawRect((int) mainHitbox.getX() + Game.WIDTH / 2, (int) mainHitbox.getY() + Game.HEIGHT / 2, (int) mainHitbox.getWidth(), (int) mainHitbox.getHeight());
//            // fps and ticks counter
//            gStatic.setColor(Color.green);
//            gStatic.drawString(game.getLastFrames() + "FPS, " + game.getLastTicks() + " ticks", 0, 10);
//            // camera move to limit
//            g.setColor(Color.yellow);
//            float xRender = cam.getX() + (float) Game.WIDTH / 2 - cam.getCameraMovementLimit();
//            float yRender = cam.getY() + (float) Game.HEIGHT / 2 - cam.getCameraMovementLimit();
//            g.drawRect((int) xRender, (int) yRender, cam.getCameraMovementLimit() * 2, cam.getCameraMovementLimit() * 2);
//            // camera rendering
//            g.setColor(Color.green);
//            g.drawRect((int) cam.getX(), (int) cam.getY(), Game.WIDTH-1, Game.HEIGHT-1);
//            // cords
//            gStatic.setColor(Color.white);
//            gStatic.drawString("XY: %s, %s".formatted((float) Math.round(p.getX() / 16 * 1000) / 1000, (float) -Math.round(p.getY() / 16 * 1000) / 1000), 5, 25);
//            gStatic.drawString("px XY: %s, %s".formatted(p.getX(), -p.getY()), 5, 40);
//        } else {
//            // fps counter
//            gStatic.setColor(Color.green);
//            gStatic.drawString(lastFrames + "FPS", 0, 10);
//        }
//        // console
//        console.render(gStatic);
    }
}
