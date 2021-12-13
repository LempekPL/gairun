package com.gairun;

import com.gairun.interfaces.Blocks;
import com.gairun.interfaces.Texture;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private final Game game;
    List<Integer> inputs;
    private Map<String, Texture> textureMap;
    private String currentTexture;
    private boolean lookingLeft = false;
    private float x;
    private float y;
    private float velX;
    private float velY;
    private float acc = 0.15F;
    // 0.05F best for ICE or air
    private float dcc = 0.10F;
    // 0.11F best for air
    // 0.15F best for walking on Earth (planet)
    private float grav = 0.15F;
    private int jumps = 1;
    private boolean flying = false;
    private boolean invisible = false;
    private boolean blockedKeys = false;
    private boolean noclip = false;
    // width, height
    private int[] offset = new int[]{16, 32};

    public Player(float x, float y, Game game) {
        this.x = x;
        this.y = y;
        this.game = game;
        pullTextures();
        currentTexture = "idle";
    }

    public void tick() {
        keyCheck();
        if (velX > 0.1) {
            lookingLeft = false;
            currentTexture = "walk";
        } else if (velX < -0.1) {
            lookingLeft = true;
            currentTexture = "walk";
        } else {
            currentTexture = "idle";
        }
        if (!flying) {
            velY -= grav;
        }
        if (!noclip) {
            collisionCheck();
        }
        y -= velY;
        x += velX;

        if (flying) {
            velX = clamp(velX, -4, 4);
            velY = clamp(velY, -4, 4);
        } else {
            velX = clamp(velX, -4, 4);
            velY = clamp(velY, -8, 5);
        }
        textureMap.get(currentTexture).runAnimation();
    }

    public void render(Graphics g) {
        if (!invisible) {
            float xRender = x - (float) textureMap.get(currentTexture).getTexture().getWidth() / 2 + (float) Game.WIDTH / 2;
            float yRender = y - (float) textureMap.get(currentTexture).getTexture().getHeight() + (float) Game.HEIGHT / 2;
            g.drawImage(flipper(textureMap.get(currentTexture).getTexture()), (int) xRender, (int) yRender, null);
        }
    }

    // this handles player collision in separate functions
    private void collisionCheck() {
        for (List<Blocks> blockRow : game.getMapController().getMapBlocks()) {
            for (Blocks block : blockRow) {
                horizontalCollision(block);
                verticalCollision(block);
                wallJump(block);
            }
        }
    }

    private void horizontalCollision(Blocks block) {
        if (getHitboxWidth().intersects(block.getHitbox())) {
            if (velX > 0) {
                velX = 0;
                x = (float) (block.getHitbox().getX() - getHitbox().getWidth() / 2);
            } else if (velX < 0) {
                velX = 0;
                x = (float) (block.getHitbox().getX() + block.getHitbox().getWidth() + getHitbox().getWidth() / 2);
            }
            if (!flying) {
                velY += 0.5;
                velY = clamp(velY, -8, -1);
            }
        }
    }

    private void verticalCollision(Blocks block) {
        if (getHitboxHeight().intersects(block.getHitbox())) {
            if (velY > 0) {
                velY = 0;
                y = (float) (block.getHitbox().getY() + block.getHitbox().getHeight() + getHitbox().getHeight());
            } else if (velY < 0) {
                velY = 0;
                y = (float) block.getHitbox().getY();
                jumps = 1;
            }
        }
    }

    private void wallJump(Blocks block) {
        if (!flying && jumps <= 0) {
            if (getWallRight().intersects(block.getHitbox()) && game.getKeyListener().checkKey(KeyEvent.VK_W)) {
                velY = 3.5f;
                velX = -2;
            }
            if (getWallLeft().intersects(block.getHitbox()) && game.getKeyListener().checkKey(KeyEvent.VK_W)) {
                velY = 3.5f;
                velX = 2;
            }
        }
    }

    private void standingOn(Blocks block) {

    }

    // this handles player movement in separate functions
    private void keyCheck() {
        inputs = game.getKeyListener().getKeysPressed();
        horizontalMove(inputs);
        if (flying) {
            verticalMove(inputs);
        } else {
            jumping();
        }
    }

    // horizontal movement
    private void horizontalMove(List<Integer> inputs) {
        if (inputs.contains(KeyEvent.VK_D) && !blockedKeys && !game.getConsole().isOpened()) {
            velX += acc;
        } else if (inputs.contains(KeyEvent.VK_A) && !blockedKeys && !game.getConsole().isOpened()) {
            velX -= acc;
        } else if (!inputs.contains(KeyEvent.VK_D) && !inputs.contains(KeyEvent.VK_A)) {
            if (velX > 0) velX -= dcc;
            else if (velX < 0) velX += dcc;
            if (velX > -dcc * 2 && velX < dcc * 2) velX = 0;
        }
    }

    // vertical movement, if flying is set to true
    private void verticalMove(List<Integer> inputs) {
        if (inputs.contains(KeyEvent.VK_W) && !blockedKeys && !game.getConsole().isOpened()) {
            velY += acc;
        } else if (inputs.contains(KeyEvent.VK_S) && !blockedKeys && !game.getConsole().isOpened()) {
            velY -= acc;
        } else if (!inputs.contains(KeyEvent.VK_W) && !inputs.contains(KeyEvent.VK_S)) {
            if (velY > 0) velY -= dcc;
            else if (velY < 0) velY += dcc;
            if (velY > -dcc * 2 && velY < dcc * 2) velY = 0;
        }
    }

    // jumping with gravitational pull
    private void jumping() {
        if (!blockedKeys && !game.getConsole().isOpened() && jumps > 0 && game.getKeyListener().checkKey(KeyEvent.VK_W)) {
            velY = 4;
            jumps--;
        }
    }

    // clamps number between min and max
    private float clamp(float value, float min, float max) {
        if (value >= max) return max;
        else if (value <= min) return min;
        return value;
    }

    public void spawnPlayer() {
        velX = 0;
        velY = 0;
        x = 8;
        y = 0;
        jumps = 1;
    }

    public void spawnPlayer(float x, float y) {
        velX = 0;
        velY = 0;
        this.x = x + 8;
        this.y = -y;
        jumps = 1;
    }

    public void spawnPlayer(JSONObject settings) {
        JSONArray pos = settings.getJSONArray("spawn");
        flying = settings.has("fly") && settings.getBoolean("fly");
        invisible = settings.has("invisible") && settings.getBoolean("invisible");
        blockedKeys = settings.has("blockedKeys") && settings.getBoolean("blockedKeys");
        noclip = settings.has("noclip") && settings.getBoolean("noclip");
        velX = 0;
        velY = 0;
        x = pos.getInt(0) * 16 + 8;
        y = pos.getInt(1) * -16;
        jumps = 1;
    }

    public Rectangle2D getHitbox() {
        return new Rectangle2D.Double(x - (double) offset[0] / 2, y - offset[1], offset[0], offset[1]);
    }

    public Rectangle2D getHitboxWidth() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Double((float) hitbox.getX() + velX, (float) hitbox.getY(), (float) hitbox.getWidth(), (float) hitbox.getHeight());
    }

    public Rectangle2D getHitboxHeight() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Double((float) hitbox.getX(), (float) hitbox.getY() - velY, (float) hitbox.getWidth(), (float) hitbox.getHeight());
    }

    private Rectangle2D getWallLeft() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Double((float) hitbox.getX() - 2, (float) hitbox.getY(), 2, (float) hitbox.getHeight());
    }

    private Rectangle2D getWallRight() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Double((float) (hitbox.getX() + hitbox.getWidth()), (float) hitbox.getY(), 2, (float) hitbox.getHeight());
    }

    private Rectangle2D getStanding() {
        Rectangle2D hitbox = getHitbox();
        return new Rectangle2D.Double((float) hitbox.getX(), (float) (hitbox.getY() - hitbox.getHeight()), (float) hitbox.getWidth(), 2);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isBlockedKeys() {
        return blockedKeys;
    }

    public void setBlockedKeys(boolean blockedKeys) {
        this.blockedKeys = blockedKeys;
    }

    public boolean isNoclip() {
        return noclip;
    }

    public void setNoclip(boolean noclip) {
        this.noclip = noclip;
    }

    public float getAcc() {
        return acc;
    }

    public void setAcc(float acc) {
        this.acc = acc;
    }

    public float getDcc() {
        return dcc;
    }

    public void setDcc(float dcc) {
        this.dcc = dcc;
    }

    public float getGrav() {
        return grav;
    }

    public void setGrav(float grav) {
        this.grav = grav;
    }

    public BufferedImage getCurrentTexture() {
        return textureMap.get(currentTexture).getTexture();
    }

    public int[] getOffset() {
        return offset;
    }

    public void setOffset(int[] offset) {
        this.offset = offset;
    }

    private void pullTextures() {
        String[] animationName = new String[]{"idle", "walk"};
        textureMap = new HashMap<>();
        for (String anName : animationName) {
            try {
                Texture tempTex;
                InputStream textureJSONfile = getClass().getClassLoader().getResourceAsStream("/data/player/%s.json".formatted(anName));
                assert textureJSONfile != null;
                JSONObject textureJSON = new JSONObject(new JSONTokener(new InputStreamReader(textureJSONfile)));
                textureJSON = textureJSON.getJSONObject("texture");
                String texturePATH = textureJSON.get("path").toString();
                InputStream imageFile = getClass().getClassLoader().getResourceAsStream("/textures/player/%s.png".formatted(texturePATH.split("/")[1]));
                assert imageFile != null;
                BufferedImage tempImage = ImageIO.read(imageFile);
                tempTex = new Texture(tempImage, textureJSON);
                textureMap.put(anName, tempTex);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage flipper(BufferedImage image) {
        if (lookingLeft) {
            AffineTransform at = new AffineTransform();
            at.concatenate(AffineTransform.getScaleInstance(-1, 1));
            at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
            BufferedImage flippedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = flippedImage.createGraphics();
            g.transform(at);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            return flippedImage;
        } else {
            return image;
        }
    }
}
