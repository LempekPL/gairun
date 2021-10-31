package com.gairun.controllers;

import com.gairun.Game;
import com.gairun.interfaces.Texture;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class BackgroundController {
    private Game game;
    private Map<String, Texture> backgroundMap = null;
    private boolean loading = false;
    private String currentBackground = null;

    public BackgroundController(Game game) {
        this.game = game;
    }

    public void tick() {
        if (loading || backgroundMap == null) return;
        if (currentBackground == null) {
            currentBackground = "main";
        }
    }

    public void render(Graphics g) {
        if (loading || backgroundMap == null || currentBackground == null) return;
        g.drawImage(backgroundMap.get(currentBackground).getTexture(), (int) game.getCamera().getX(), (int) -game.getCamera().getY(), null);
    }

    public void loadNewBackgrounds(JSONObject backgroundsJSON) {
        loading = true;
        currentBackground = null;
        backgroundMap = new HashMap<>();
        Iterator<String> bgs = backgroundsJSON.keys();
        while (bgs.hasNext()) {
            String nameBG = bgs.next();
            JSONObject dataBG = backgroundsJSON.getJSONObject(nameBG);
            if (Objects.equals(dataBG.getString("type"), "static")) {
                Texture tempTex;
                BufferedImage tempImage = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_ARGB);
                Graphics tempG = tempImage.getGraphics();
                JSONObject colorBG = dataBG.getJSONObject("color");
                tempG.setColor(new Color(colorBG.getInt("R"), colorBG.getInt("G"), colorBG.getInt("B"), colorBG.getInt("A")));
                tempG.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
                tempG.dispose();
                tempTex = new Texture(tempImage, dataBG);
                backgroundMap.put(nameBG, tempTex);
            } else if (Objects.equals(dataBG.getString("type"), "dynamic")) {

            } else if (Objects.equals(dataBG.getString("type"), "image") || Objects.equals(dataBG.getString("type"), "animation")) {
//            try {
//                Texture tempTex;
//                FileReader textureJSONfile = new FileReader("res/data/player/%s.json".formatted(anName));
//                JSONObject textureJSON = new JSONObject(new JSONTokener(textureJSONfile));
//                textureJSON = textureJSON.getJSONObject("texture");
//                String texturePATH = textureJSON.get("path").toString();
//                File imageFile = new File("res/textures/player/%s.png".formatted(texturePATH.split("/")[1]));
//                BufferedImage tempImage = ImageIO.read(imageFile);
//                tempTex = new Texture(tempImage, textureJSON);
//                textureMap.put(anName, tempTex);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            }
        }
        loading = false;
    }

    public String getCurrentBackground() {
        return currentBackground;
    }

    public void setCurrentBackground(String currentBackground) {
        this.currentBackground = currentBackground;
    }
}
