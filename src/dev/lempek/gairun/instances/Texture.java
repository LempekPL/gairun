package dev.lempek.gairun.instances;

import dev.lempek.gairun.enums.TextureEnum;
import dev.lempek.gairun.exceptions.TextureException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class Texture {
    private BufferedImage image;
    private BufferedImage currentImage;
    private List<BufferedImage> imageFrames;
    private JSONObject imageData;
    private int[] frameOrder;
    private int index = 0;
    private int speed = 0;
    private int imageFrame = 0;
    private boolean animated = false;
    private boolean paused = false;

    public Texture(String path) throws TextureException, IOException {
        loadTexture(path);
    }

    public void tick() {
        if (!animated || paused) return;
    }

    private void loadTexture(String path) throws TextureException, IOException {
        String pack = path.split(":")[0];
        path = path.split(":")[1];
        String place = Objects.equals(pack, "gairun") ? "res" : "resourcePacks/%s".formatted(pack);
        // try to get data pack
        InputStream textureJSONfile = getClass().getClassLoader().getResourceAsStream("/%s/data/%s.json".formatted(place, path));
        if (textureJSONfile == null) {
            throw new TextureException(TextureEnum.TEXTURE_DATA_NOT_FOUNT);
        }
        JSONObject textureJSON = new JSONObject(new JSONTokener(new InputStreamReader(textureJSONfile)));
        if (!textureJSON.has("texture") || !textureJSON.getJSONObject("texture").has("path")) {
            // if there is no texture set texture to invisible
            image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            return;
        }
        // get texture from the data file
        String texturePathUnformatted = textureJSON.getJSONObject("texture").getString("path");
        String placePack = Objects.equals(texturePathUnformatted.split(":")[0], "gairun") ? "res" : "resourcePacks/%s".formatted(texturePathUnformatted.split(":")[0]);
        String texturePath = "/%s/textures/%s.png".formatted(placePack, texturePathUnformatted.split(":")[1]);
        BufferedImage texturePNGfile;
        // try to get file, if not fallback to error png
        try {
            texturePNGfile = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(texturePath)));
        } catch (IOException ignored) {
            InputStream errorJSONfile = getClass().getClassLoader().getResourceAsStream("/res/data/error.json");
            // if there is no data file try to get the texture
            if (errorJSONfile == null) {
                image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("/res/textures/blocks/error.png")));
                return;
            }
            JSONObject errorJSON = new JSONObject(new JSONTokener(new InputStreamReader(errorJSONfile)));
            // if for some reason there is no texture or path try to get the texture
            if (!errorJSON.has("texture") || !errorJSON.getJSONObject("texture").has("path")) {
                image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("/res/textures/blocks/error.png")));
                return;
            }
            String errorPathUnformatted = errorJSON.getJSONObject("texture").getString("path");
            String errorPlacePack = Objects.equals(errorPathUnformatted.split(":")[0], "gairun") ? "res" : "resourcePacks/%s".formatted(errorPathUnformatted.split(":")[0]);
            String errorPath = "/%s/textures/%s.png".formatted(errorPlacePack, errorPathUnformatted.split(":")[1]);
            image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(errorPath)));
            return;
        }
        // check if texture is animated
        if (textureJSON.getJSONObject("texture").has("animation")) {
            // get animation object
            JSONObject anim = textureJSON.getJSONObject("texture").getJSONObject("animation");
            // set default values
            if (anim.has("defaults")) {
                if (anim.getJSONObject("defaults").has("frame")) {
                    imageFrame = anim.getJSONObject("defaults").getInt("frame");
                }
                if (anim.getJSONObject("defaults").has("paused")) {
                    paused = anim.getJSONObject("defaults").getBoolean("paused");
                }
            }
            speed = anim.has("speed") ? anim.getInt("speed") : 60;
            int frameOrderLength = anim.getJSONArray("frameOrder").length();
            frameOrder = new int[frameOrderLength];
            for (int i = 0; i < frameOrderLength; i++) {
                frameOrder[i] = anim.getJSONArray("frameOrder").getInt(i);
            }

            animated = true;
            return;
        }
        // if there is no animation try to set texture based on given size
        if (textureJSON.getJSONObject("texture").has("width") && textureJSON.getJSONObject("texture").has("height")) {
            image = texturePNGfile.getSubimage(0, 0, textureJSON.getJSONObject("texture").getInt("width"), textureJSON.getJSONObject("texture").getInt("height"));
            return;
        }
        // fail safe if there is no size
        image = texturePNGfile;
    }
}
