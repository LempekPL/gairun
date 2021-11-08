package com.gairun.controllers;

import com.gairun.interfaces.Texture;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TextureController {
    private Map<String, Texture> textureMap;
    private Map<String, int[]> hitboxMap;

    public void loadTextureList(Map<String, Object> textureList) {
        textureMap = new HashMap<>();
        hitboxMap = new HashMap<>();
        JSONObject errorJSON = null;
        JSONObject errorJSONtex = null;
        try {
            FileReader errorJSONfile = new FileReader("res/data/block/error.json");
            errorJSON = new JSONObject(new JSONTokener(errorJSONfile));
            errorJSONtex = errorJSON.getJSONObject("texture");
            String errorPATH = errorJSONtex.getString("path");
            textureMap.put("error", new Texture(ImageIO.read(new File("res/textures/block/%s.png".formatted(errorPATH.split("/")[1]))), errorJSONtex));
            if (errorJSON.has("hitbox")) {
                JSONObject errorJSONhitbox = errorJSON.getJSONObject("hitbox");
                if (errorJSONhitbox.has("offset")) {
                    hitboxMap.put("error", new int[]{errorJSONhitbox.getJSONArray("offset").getInt(0), errorJSONhitbox.getJSONArray("offset").getInt(1), errorJSONhitbox.getInt("width"), errorJSONhitbox.getInt("height")});
                } else {
                    hitboxMap.put("error", new int[]{0, 0, errorJSONhitbox.getInt("width"), errorJSONhitbox.getInt("height")});
                }
            } else {
                hitboxMap.put("error", new int[]{0, 0, 16, 16});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (var texture : textureList.entrySet()) {
            String[] place = texture.getValue().toString().split("/");
            if (Objects.equals(place[0], "res")) {
                try {
                    Texture tempTex;
                    int[] tempHitbox;
                    if (new File("res/data/block/%s.json".formatted(place[1])).isFile()) {
                        FileReader textureJSONfile = new FileReader("res/data/block/%s.json".formatted(place[1]));
                        JSONObject textureJSON = new JSONObject(new JSONTokener(textureJSONfile));
                        JSONObject textureJSONtex = textureJSON.getJSONObject("texture");
                        String texturePATH = textureJSONtex.getString("path");
                        // TODO: add custom pack/resPackName/block.png texture ability
                        File imageFile = new File("res/textures/block/%s.png".formatted(texturePATH.split("/")[1]));
                        BufferedImage tempImage = ImageIO.read(imageFile);
                        tempTex = new Texture(tempImage, textureJSONtex);
                        if (textureJSON.has("hitbox")) {
                            JSONObject textureJSONhitbox = textureJSON.getJSONObject("hitbox");
                            if (textureJSONhitbox.has("offset")) {
                                tempHitbox = new int[]{textureJSONhitbox.getJSONArray("offset").getInt(0), textureJSONhitbox.getJSONArray("offset").getInt(1), textureJSONhitbox.getInt("width"), textureJSONhitbox.getInt("height")};
                            } else {
                                tempHitbox = new int[]{0,0,textureJSONhitbox.getInt("width"), textureJSONhitbox.getInt("height")};
                            }
                        } else {
                            tempHitbox = new int[]{0, 0, 0, 0};
                        }
                    } else {
                        tempTex = new Texture(ImageIO.read(new File("res/textures/block/error.png")), errorJSONtex);
                        assert errorJSON != null;
                        if (errorJSON.has("hitbox")) {
                            JSONObject errorJSONhitbox = errorJSON.getJSONObject("hitbox");
                            if (errorJSONhitbox.has("offset")) {
                                tempHitbox = new int[]{errorJSONhitbox.getJSONArray("offset").getInt(0), errorJSONhitbox.getJSONArray("offset").getInt(1), errorJSONhitbox.getInt("width"), errorJSONhitbox.getInt("height")};
                            } else {
                                tempHitbox = new int[]{0,0,errorJSON.getJSONObject("hitbox").getInt("width"), errorJSON.getJSONObject("hitbox").getInt("height")};
                            }
                        } else {
                            tempHitbox = new int[]{0, 0, 16, 16};
                        }
                    }
                    textureMap.put(texture.getKey(), tempTex);
                    hitboxMap.put(texture.getKey(), tempHitbox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, Texture> getTextureMap() {
        return textureMap;
    }

    public Map<String, int[]> getHitboxMap() {
        return hitboxMap;
    }
}
