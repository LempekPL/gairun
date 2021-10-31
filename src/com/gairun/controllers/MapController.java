package com.gairun.controllers;

import com.gairun.Game;
import com.gairun.interfaces.BlockType;
import com.gairun.interfaces.Blocks;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class MapController {
    private final Game game;
    private TextureController textureController;
    private List<List<Blocks>> blocks;
    private String currentSet;
    private String currentMap;
    private boolean loading = true;
    private JSONObject backgroundsJSON;

    public MapController(Game game) {
        this.game = game;
    }

    public void tick() {
        if (!loading) {
            textureController.getTextureMap().forEach((key, tex) -> {
                tex.runAnimation();
            });
        }
    }

    public void render(Graphics g) {
        if (!loading && blocks.size() > 0) {
            for (List<Blocks> blockList : blocks) {
                for (Blocks block : blockList) {
                    block.render(g);
                }
            }
        }
    }

    public void loadMap(String mapSet, String mapId) {
        loading = true;
        List<List<String>> tempMapLayout = new ArrayList<>();
        List<List<Blocks>> tempBlocks = new ArrayList<>();
        backgroundsJSON = null;
        JSONObject mapJSON = null;
        try (BufferedReader br = new BufferedReader(new FileReader("res/maps/%s/%s.csv".formatted(mapSet, mapId)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                tempMapLayout.add(Arrays.asList(values));
            }
            FileReader mapJSONfile = new FileReader("res/maps/%s/%s.json".formatted(mapSet, mapId));
            mapJSON = new JSONObject(new JSONTokener(mapJSONfile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mapJSON == null) return;
        // TODO: add resource pack textures

        textureController = new TextureController();
        Map<String, Object> mapOfBlocks = mapJSON.getJSONObject("usedBlocks").toMap();
        textureController.loadTextureList(mapOfBlocks);

        for (int i = 0; i < tempMapLayout.size(); i++) {
            List<Blocks> tempBlocksList = new ArrayList<>();
            for (int j = 0; j < tempMapLayout.get(i).size(); j++) {
                addBlocks(tempMapLayout, tempBlocksList, textureController, i, j);
            }
            tempBlocks.add(tempBlocksList);
        }
        blocks = tempBlocks;
        currentSet = mapSet;
        currentMap = mapId;

        JSONObject playerSettings = mapJSON.getJSONObject("playerSettings");
        game.getPlayer().spawnPlayer(playerSettings);
        game.getCamera().centerOnPlayer();

        if (mapJSON.has("backgrounds")) {
            backgroundsJSON = mapJSON.getJSONObject("backgrounds");
            game.getBackgroundController().loadNewBackgrounds(backgroundsJSON);
        }
        loading = false;
    }

    private void addBlocks(List<List<String>> tempMapLayout, List<Blocks> tempBlocksList, TextureController textureController, int i, int j) {
        if (!Objects.equals(tempMapLayout.get(i).get(j), "-")) {
            if (tempMapLayout.get(i).get(j).contains("|")) {
                String[] tempOneBlockList = tempMapLayout.get(i).get(j).split("\\|");
                for (String block : tempOneBlockList) {
                    createBlock(block, tempMapLayout, tempBlocksList, textureController, i, j);
                }
            } else {
                createBlock(tempMapLayout.get(i).get(j), tempMapLayout, tempBlocksList, textureController, i, j);
            }
        }
    }

    private void createBlock(String block, List<List<String>> tempMapLayout, List<Blocks> tempBlocksList, TextureController textureController, int i, int j) {
        if (textureController.getTextureMap().containsKey(block)) {
            tempBlocksList.add(new Blocks(j * 16, -i * 16 + tempMapLayout.size() * 16, textureController.getTextureMap().get(block), BlockType.BLOCK, game));
        } else {
            tempBlocksList.add(new Blocks(j * 16, -i * 16 + tempMapLayout.size() * 16, textureController.getTextureMap().get("error"), BlockType.BLOCK, game));
        }
    }

    public List<List<Blocks>> getMapBlocks() {
        return blocks;
    }

    public String getCurrentSet() {
        return currentSet;
    }

    public String getCurrentMapId() {
        return currentMap;
    }

    public JSONObject getBackgroundsJSON() {
        return backgroundsJSON;
    }

    //    public void loadMap(String resourcePackName, String mapSet, String mapId) {
//        File mapCSVfile = new File("res/maps");
//
//    }
}
