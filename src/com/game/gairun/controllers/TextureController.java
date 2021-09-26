package com.game.gairun.controllers;

import com.game.gairun.Game;
import com.game.gairun.interfaces.Texture;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
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
            FileReader errorJSONfile = new FileReader("res/textures/error.json");
            errorJSON = new JSONObject(new JSONTokener(errorJSONfile));
            textureMap.put("error", new Texture(ImageIO.read(new File("res/textures/error.png")), errorJSON));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (var texture : textureList.entrySet()) {
            String[] place = texture.getValue().toString().split("/");
            if (Objects.equals(place[0], "res")) {
                try {
                    Texture tempTex;
                    File imageFile = new File("res/textures/%s.png".formatted(place[1]));
                    if (imageFile.isFile() && new File("res/textures/%s.json".formatted(place[1])).isFile()) {
                        FileReader textureJSONfile = new FileReader("res/textures/%s.json".formatted(place[1]));
                        BufferedImage tempImage = ImageIO.read(imageFile);
                        JSONObject textureJSON = new JSONObject(new JSONTokener(textureJSONfile));
                        tempTex = new Texture(tempImage, textureJSON);
                    } else {
                        tempTex = new Texture(ImageIO.read(new File("res/textures/error.png")), errorJSON);
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
