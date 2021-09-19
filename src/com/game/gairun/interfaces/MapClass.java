package com.game.gairun.interfaces;

import com.game.gairun.libs.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapClass {
    private String mapId, mapSet;
    private int mapCenterX, mapCenterY;
    private BufferedImage textureImage, mapImage;
    private BufferedImage blockTex, blockTex2, blockTex3;
    private List<List<String>> mapLayout;

    public MapClass(String mapId, String mapSet) {
        this.mapId = mapId;
        this.mapSet = mapSet;
        loadMap();
    }

    private void loadMap() {
        mapLayout = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("res/maps/%s/%s.csv".formatted(mapSet, mapId)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                mapLayout.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // texture manager here
        ImageHandler ih = new ImageHandler("block");
        blockTex = ih.grabImage(0, 0, 16, 16);
        ih = new ImageHandler("player");
        blockTex2 = ih.grabImage(0, 0, 16, 16);
        blockTex3 = ih.grabImage(0, 1, 16, 16);
        // ^ there

        mapImage = new BufferedImage(16 * mapLayout.get(0).size(), 16 * mapLayout.size(), BufferedImage.TYPE_INT_ARGB);
        Graphics mapGraphics = mapImage.getGraphics();
        for (int i = 0; i < mapLayout.size() && i < 2800; i++) {
            for (int j = 0; j < mapLayout.get(i).size() && j < 2800; j++) {

                if (Objects.equals(mapLayout.get(i).get(j), "P")) {
                    mapCenterX = j * 16 + 8;
                    mapCenterY = -i * 16 - 16;
                }
                if (Objects.equals(mapLayout.get(i).get(j), "B")) {
                    mapGraphics.drawImage(blockTex, j * 16, i * 16, null);
                } else if (Objects.equals(mapLayout.get(i).get(j), "C")) {
                    mapGraphics.drawImage(blockTex2, j * 16, i * 16, null);
                } else if (Objects.equals(mapLayout.get(i).get(j), "D")) {
                    mapGraphics.drawImage(blockTex3, j * 16, i * 16, null);
                }
//                future plans
//                mapGraphics.drawImage(textureList.get(tempMapLayout.get(i).get(j)), j * 16, i * 16, null);
            }
        }
        mapGraphics.dispose();
    }

    public String getMapId() {
        return mapId;
    }

    public String getMapSet() {
        return mapSet;
    }

    public List<List<String>> getMapLayout() {
        return mapLayout;
    }

    public BufferedImage getMapImage() {
        return mapImage;
    }

    public int getMapCenterX() {
        return mapCenterX;
    }

    public int getMapCenterY() {
        return mapCenterY;
    }
}
