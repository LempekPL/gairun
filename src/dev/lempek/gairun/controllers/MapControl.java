package dev.lempek.gairun.controllers;

import dev.lempek.gairun.Game;
import dev.lempek.gairun.enums.LoadMapEnum;
import dev.lempek.gairun.exceptions.LoadMapException;
import dev.lempek.gairun.exceptions.TextureException;
import dev.lempek.gairun.instances.Texture;
import dev.lempek.gairun.templates.Block;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.*;

public class MapControl {
    private final Game game;
    private String currentId;
    private String currentSet;
    private String currentPack = "gairun";
    private JSONObject mapPackData;
    private JSONObject backgroundsJSON;
    private List<Texture> backgrounds;
    private List<Texture> textures;
    private List<Block> blocks;
    private boolean loading = true;
    // save line that got error
    private int lines = 0;
    private int columns = 0;

    public MapControl(Game game) {
        this.game = game;
        InputStream mapPackISFile = getClass().getClassLoader().getResourceAsStream("/res/maps/mapPack.json");
        assert mapPackISFile != null;
        mapPackData = new JSONObject(new JSONTokener(new InputStreamReader(mapPackISFile)));
    }

    public void tick() {
        if (loading) return;
        for (Texture t : textures) {
            t.tick();
        }
//        for (List<Block> blockList : blocks) {
//            for (Block block : blockList) {
//                block.tick();
//            }
//        }
    }

    public void render(Graphics g) {
        if (loading || blocks.size() <= 0) return;
        for (Block block : blocks) {
//            if (blockList.get(i).getY() > game.getCamera() )
            block.render(g);
        }
    }

    // set current pack and load map from another function
    public LoadMapEnum loadMap(String id, String set, String pack) {
        if (!Objects.equals(pack, currentPack)) {
            String place = Objects.equals(pack, "gairun") ? "res" : "resourcePacks/%s".formatted(pack);
            InputStream mapPackISFile = getClass().getClassLoader().getResourceAsStream("/%s/maps/mapPack.json".formatted(place));
            if (mapPackISFile == null) {
                return LoadMapEnum.MAP_PACK_NOT_FOUND;
            }
            mapPackData = new JSONObject(new JSONTokener(new InputStreamReader(mapPackISFile)));
            currentPack = pack;
        }
        // no need for additional checks, because it's always checked when map pack changes
        return loadMap(id, set);
    }

    // set current set and load map from another function
    public LoadMapEnum loadMap(String id, String set) {
        if (!Objects.equals(set, currentSet)) {
            if (currentSet == null || !mapPackData.getJSONObject("maps").has(set)) {
                if (mapPackData.has("default") && mapPackData.getJSONObject("default").has("returnToDefault") && mapPackData.getJSONObject("default").getBoolean("returnToDefault")) {
                    if (Objects.equals(mapPackData.getJSONObject("default").getString("set"), set)) {
                        return LoadMapEnum.DEFAULT_SET_NOT_FOUND;
                    }
                    return loadMap(id, mapPackData.getJSONObject("default").getString("set"));
                }
                return LoadMapEnum.SET_NOT_FOUND;
            }
            currentSet = set;
        }
        // no need for additional checks, because it's always checked when set changes
        return loadMap(id);
    }

    // set current id and load map from another function
    public LoadMapEnum loadMap(String id) {
        if (!Objects.equals(id, currentId)) {
            if (currentId == null || !mapPackData.getJSONObject("maps").getJSONArray(currentSet).toList().contains(id)) {
                if (mapPackData.has("default") && mapPackData.getJSONObject("default").has("returnToDefault") && mapPackData.getJSONObject("default").getBoolean("returnToDefault")) {
                    if (Objects.equals(mapPackData.getJSONObject("default").getString("id"), id)) {
                        return LoadMapEnum.DEFAULT_ID_NOT_FOUND;
                    }
                    return loadMap(mapPackData.getJSONObject("default").getString("id"), mapPackData.getJSONObject("default").getString("set"));
                }
                return LoadMapEnum.ID_NOT_FOUND;
            }
            currentId = id;
        }
        // function to load map from a file
        // TODO: make it throw exception further
        try {
            return parseMap();
        } catch (LoadMapException e) {
            return e.getErr();
        } catch (TextureException e) {
            return new LoadMapException(LoadMapEnum.TEXTURE_ERROR, e.getErr()).getErr();
        }
    }

    private LoadMapEnum parseMap() throws LoadMapException, TextureException {
        String place = Objects.equals(currentPack, "gairun") ? "res" : "resourcePacks/%s".formatted(currentPack);
        InputStream mapFile = getClass().getClassLoader().getResourceAsStream("/%s/maps/%s/%s.csv".formatted(place, currentSet, currentId));
        if (mapFile == null) {
            throw new LoadMapException(LoadMapEnum.MAP_FILE_NOT_FOUND);
        }
        InputStream mapJSONfile = getClass().getClassLoader().getResourceAsStream("/%s/maps/%s/%s.json".formatted(place, currentSet, currentId));
        if (mapJSONfile == null) {
            throw new LoadMapException(LoadMapEnum.MAP_SETTINGS_FILE_NOT_FOUND);
        }
        JSONObject mapJSON = new JSONObject(new JSONTokener(new InputStreamReader(mapJSONfile)));
        loading = true;
        List<List<String>> tempMapLayout = new ArrayList<>();
        List<Block> tempBlocks = new ArrayList<>();
        backgroundsJSON = null;
        lines = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(mapFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines++;
                String[] values = line.split(",");
                tempMapLayout.add(Arrays.asList(values));
            }
        } catch (IOException ignore) {
            throw new LoadMapException(LoadMapEnum.READING_ERROR);
        }

        Map<String, Object> mapOfBlocks = mapJSON.getJSONObject("usedBlocks").toMap();
        Map<String, Texture> mapOfTextures = new HashMap<>();
        for (var block : mapOfBlocks.entrySet()) {
            try {
                mapOfTextures.put(block.getKey(), new Texture(block.getValue().toString()));
            } catch (TextureException e) {
                throw new TextureException(e.getErr());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return LoadMapEnum.OK;
    }
}
