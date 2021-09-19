package com.game.gairun;

import com.game.gairun.interfaces.MapClass;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.*;

public class MapController {
    private Map<String, Map<String, MapClass>> mapMainSets;
    private String currentSet, currentMap;
    private boolean loadedMap;
    private Game game;

    public MapController(Game game) {
        this.game = game;
        loadMainSets();
        loadMap("main", "1");
    }

    public void render(Graphics g, Camera cam) {
        if (loadedMap) {
            double xRender = (double) cam.getViewportWidth() / 2 - cam.getX() - mapMainSets.get(currentSet).get(currentMap).getMapCenterX();
            double yRender = (double) cam.getViewportHeight() / 2 + cam.getY() + mapMainSets.get(currentSet).get(currentMap).getMapCenterY();
            g.drawImage(mapMainSets.get(currentSet).get(currentMap).getMapImage(), (int) xRender, (int) yRender, null);
            if (cam.isDebug()) {
                List<List<String>> listLayout = mapMainSets.get(currentSet).get(currentMap).getMapLayout();
                for (int i = 0; i < listLayout.size(); i++) {
                    for (int j = 0; j < listLayout.get(i).size(); j++) {
                        if (!Objects.equals(listLayout.get(i).get(j), "A") && !Objects.equals(listLayout.get(i).get(j), "P")) {
                            g.drawRect(j * 16 + (int) xRender, i * 16 + (int) yRender, 15, 15);
                        }
                    }
                }
            }
        }
    }

    public void loadMap(String mapSet, String mapId) {
        loadedMap = false;
        currentSet = mapSet;
        currentMap = mapId;
        game.getPlayer().resetPlayer();
        game.getCamera().centerOnPlayer();
        loadedMap = true;
    }

    private void loadMainSets() {
        mapMainSets = new HashMap<>();
        File mapSetDir = new File("res/maps");
        String[] listOfSets = mapSetDir.list((dir, name) -> new File(dir, name).isDirectory());
        assert listOfSets != null;
        for (String mapSetDirNames : listOfSets) {
            Map<String, MapClass> mapsInsideMapSet = new HashMap<>();
            File mapsNamesDir = new File("res/maps/%s".formatted(mapSetDirNames));
            String[] mapsNames = mapsNamesDir.list((dir, name) -> name.endsWith(".csv"));
            assert mapsNames != null;
            for (String mapName : mapsNames) {
                String formattedMapName = mapName.replaceAll(".csv", "");
                mapsInsideMapSet.put(formattedMapName, new MapClass(formattedMapName, mapSetDirNames));
            }
            mapMainSets.put(mapSetDirNames, mapsInsideMapSet);
        }
    }

    public MapClass getCurrentMap() {
        return mapMainSets.get(currentSet).get(currentMap);
    }
}
