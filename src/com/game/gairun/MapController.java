package com.game.gairun;

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

public class MapController {
    private List<List<String>> mapLayout;
    private BufferedImage mapImage;
    private boolean loadedMap;
    private int mapMiddleX, mapMiddleY;

    public MapController(String startMap, Game game) {
        loadMap(startMap, game);
    }

    public void render(Graphics g, Camera cam) {
        if (loadedMap) {
            double xRender = mapMiddleX + (double) cam.getViewportWidth() / 2 - cam.getX();
            double yRender = mapMiddleY + (double) cam.getViewportHeight() / 2 + cam.getY();
            g.drawImage(mapImage, (int) xRender, (int) yRender, null);
        }
    }

    public void loadMap(String mapId, Game game) {
        loadedMap = false;
        mapLayout = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("res/maps/%s.csv".formatted(mapId)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                mapLayout.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(mapLayout);
        System.out.println(mapLayout.size());
        System.out.println(mapLayout.get(0).size());
        mapImage = new BufferedImage(16 * mapLayout.get(0).size(), 16 * mapLayout.size(), BufferedImage.TYPE_INT_ARGB);
        Graphics mapGraphics = mapImage.getGraphics();

        // texture manager here
        ImageHandler ih = new ImageHandler("block");
        BufferedImage blockTex = ih.grabImage(0, 0, 16, 16);
        // ^ there

        for (int i = 0; i < mapLayout.size(); i++) {
            for (int j = 0; j < mapLayout.get(i).size(); j++) {
                if (Objects.equals(mapLayout.get(i).get(j), "B")) {
                    mapGraphics.drawImage(blockTex, j * 16, i * 16, null);
                } else if (Objects.equals(mapLayout.get(i).get(j), "P")) {
                    mapMiddleX = j * 16;
                    mapMiddleY = i * 16;
                    game.getPlayer().setX(mapMiddleX);
                    game.getPlayer().setY(mapMiddleY);
                }
            }
        }
        mapGraphics.dispose();
        loadedMap = true;
    }
}
