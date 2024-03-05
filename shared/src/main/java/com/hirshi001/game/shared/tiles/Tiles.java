package com.hirshi001.game.shared.tiles;


import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.hirshi001.game.shared.registry.DefaultRegistry;
import com.hirshi001.game.shared.registry.MapRegistry;
import com.hirshi001.game.shared.registry.Registry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Tiles {

    private static Tiles INSTANCE;

    public static Tiles getInstance(){
        return INSTANCE;
    }

    public static void setInstance(Tiles tiles){
        INSTANCE = tiles;
    }

    public final Registry<Tile> tileRegistry = new MapRegistry<>();
    private int nextID = 0;


    public Pixmap tiles;
    private final int tileSize;
    private final int columns = 10;
    private int rows = 10;


    private IntIntMap tileIdToTextureId = new IntIntMap();
    private Queue<Integer> freeTextureIds = new ArrayDeque<>();

    public Tiles(int tileSize) {
        this.tileSize = tileSize;
        tiles = new Pixmap(tileSize * columns, tileSize * rows, Pixmap.Format.RGBA8888);
        for(int i = 0; i < columns * rows; i++){
            freeTextureIds.add(i);
        }
    }

    public Tile register(Tile tile, Pixmap texture, int id) {
        Integer textureId = freeTextureIds.poll();
        if(textureId == null){
            resize(rows + 1);
        }
        tileIdToTextureId.put(id, textureId);
        updateTexture(textureId, texture);
        tileRegistry.register(tile, id);
        return tile;
    }

    public Tile register(Tile tile, Pixmap texture) {
        while (tileRegistry.get(nextID) != null) {
            nextID++;
        }
        return register(tile, texture, nextID);
    }

    public void updateTexture(int textureId, Pixmap texture) {
        int x = (textureId % columns) * tileSize;
        int y = (textureId / columns) * tileSize;
        tiles.drawPixmap(texture, x, y);
    }

    public void resize(int newRows) {
        if(newRows <= rows) return;

        rows = newRows;
        Pixmap newTiles = new Pixmap(tileSize * columns, tileSize * newRows, Pixmap.Format.RGBA8888);
        newTiles.drawPixmap(tiles, 0, 0);

        tiles.dispose();
        tiles = newTiles;
    }


}
