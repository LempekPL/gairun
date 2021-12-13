package com.gairun.controllers;

import com.gairun.Game;
import com.gairun.interfaces.BlockType;
import com.gairun.interfaces.Blocks;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.*;
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
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("/maps/%s/%s.csv".formatted(mapSet, mapId)))))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                tempMapLayout.add(Arrays.asList(values));
            }
            InputStream mapJSONfile = getClass().getClassLoader().getResourceAsStream("/maps/%s/%s.json".formatted(mapSet, mapId));
            assert mapJSONfile != null;
            mapJSON = new JSONObject(new JSONTokener(new InputStreamReader(mapJSONfile)));
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
                if (!Objects.equals(tempMapLayout.get(i).get(j), "-")) {
                    addBlocks(i, j, textureController, tempMapLayout, tempBlocksList);
                }
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

    private void addBlocks(int i, int j, TextureController textureController, List<List<String>> tempMapLayout, List<Blocks> tempBlocksList) {
        if (tempMapLayout.get(i).get(j).contains("|")) {
            String[] tempOneBlockList = tempMapLayout.get(i).get(j).split("\\|");
            for (String block : tempOneBlockList) {
                tempBlocksList.add(createBlock(i, j, block, textureController, tempMapLayout));
            }
        } else {
            tempBlocksList.add(createBlock(i, j, tempMapLayout.get(i).get(j), textureController, tempMapLayout));
        }
    }

    private Blocks createBlock(int i, int j, String block, TextureController textureController, List<List<String>> tempMapLayout) {
        if (textureController.getTextureMap().containsKey(block)) {
            return new Blocks(j * 16, i * 16 - (tempMapLayout.size()-1) * 16, textureController.getHitboxMap().get(block), textureController.getTextureMap().get(block), BlockType.BLOCK, game);
        } else {
            return new Blocks(j * 16, i * 16 - (tempMapLayout.size()-1) * 16, textureController.getHitboxMap().get("error"), textureController.getTextureMap().get("error"), BlockType.BLOCK, game);
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
