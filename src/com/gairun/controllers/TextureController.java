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

    public void loadTextureList(Map<String, Object> textureList) {
        textureMap = new HashMap<>();
        JSONObject errorJSON = null;
        try {
            FileReader errorJSONfile = new FileReader("res/data/block/error.json");
            errorJSON = new JSONObject(new JSONTokener(errorJSONfile));
            errorJSON = errorJSON.getJSONObject("texture");
            String errorPATH = errorJSON.getString("path");
            textureMap.put("error", new Texture(ImageIO.read(new File("res/textures/block/%s.png".formatted(errorPATH.split("/")[1]))), errorJSON));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (var texture : textureList.entrySet()) {
            String[] place = texture.getValue().toString().split("/");
            if (Objects.equals(place[0], "res")) {
                try {
                    Texture tempTex;
                    if (new File("res/data/block/%s.json".formatted(place[1])).isFile()) {
                        FileReader textureJSONfile = new FileReader("res/data/block/%s.json".formatted(place[1]));
                        JSONObject textureJSON = new JSONObject(new JSONTokener(textureJSONfile));
                        textureJSON = textureJSON.getJSONObject("texture");
                        String texturePATH = textureJSON.getString("path");
                        // TODO: add custom pack/resPackName/block.png texture ability
                        File imageFile = new File("res/textures/block/%s.png".formatted(texturePATH.split("/")[1]));
                        BufferedImage tempImage = ImageIO.read(imageFile);
                        tempTex = new Texture(tempImage, textureJSON);
                    } else {
                        tempTex = new Texture(ImageIO.read(new File("res/textures/block/error.png")), errorJSON);
                    }
                    textureMap.put(texture.getKey(), tempTex);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, Texture> getTextureMap() {
        return textureMap;
    }
}
