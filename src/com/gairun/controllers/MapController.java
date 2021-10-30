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
import java.util.*;
import java.util.List;

public class MapController {
    private List<List<Blocks>> blocks;
    private String currentSet;
    private String currentMap;
    private boolean loading = true;
    private final Game game;

    public MapController(Game game) {
        this.game = game;
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

    public void tick() {
        if (!loading && blocks.size() > 0) {
            for (List<Blocks> blockList : blocks) {
                for (Blocks block : blockList) {
                    block.tick();
                }
            }
        }
    }

    public void loadMap(String mapSet, String mapId) {
        loading = true;
        List<List<String>> tempMapLayout = new ArrayList<>();
        List<List<Blocks>> tempBlocks = new ArrayList<>();
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

        TextureController textureController = new TextureController();
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

        JSONArray playerPos = mapJSON.getJSONArray("playerSpawn");
        game.getPlayer().spawnPlayer((int) playerPos.get(0) * 16, (int) playerPos.get(1) * 16);
        game.getCamera().centerOnPlayer();
        loading = false;
    }

    private void addBlocks(List<List<String>> tempMapLayout, List<Blocks> tempBlocksList, TextureController textureController, int i, int j) {
        if (!Objects.equals(tempMapLayout.get(i).get(j), "-")) {
            if (tempMapLayout.get(i).get(j).contains("|")) {
                String[] tempOneBlockList = tempMapLayout.get(i).get(j).split("\\|");
                for (String block: tempOneBlockList) {
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

    //    public void loadMap(String resourcePackName, String mapSet, String mapId) {
//        File mapCSVfile = new File("res/maps");
//
//    }
}
