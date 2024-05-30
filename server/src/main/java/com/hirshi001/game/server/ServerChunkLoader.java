package com.hirshi001.game.server;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.hirshi001.game.shared.entities.Stone;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.game.shared.util.HashedPoint;
import java.security.SecureRandom;

import java.util.*;

public class ServerChunkLoader implements ChunkLoader {

    OpenSimplexNoise noise = new OpenSimplexNoise(0);
    Random random = new SecureRandom();

    Set<HashedPoint> loadedChunks = new HashSet<>();
    Map<HashedPoint, Array<GridPoint2>> resourcePoints = new HashMap<>();
    private final int chunkSize;

    Color[] colors = new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PURPLE, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.PINK, Color.LIME, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.GRAY, Color.WHITE, Color.BLACK};

    public ServerChunkLoader(int chunkSize) {
        this.chunkSize = chunkSize;

        int radius = 1024;
        GridPoint2 center = new GridPoint2(radius, radius);
        OrderedMap<GridPoint2, Array<GridPoint2>> resourcePointMap = StandalonePoissonDisk.sampleCircle(center, radius, 32, 2 * radius, 2 * radius);
        // OrderedMap<GridPoint2, Array<GridPoint2>> resourcePointMap = StandalonePoissonDisk.sampleRectangle(new GridPoint2(0, 0), new GridPoint2(2048, 2048), 10);

        // resourcePointMap.removeIndex(0); // remove center point

        GridPoint2 offset = new GridPoint2();
        for (GridPoint2 point : resourcePointMap.keys()) {
            // if(Math.random()<0.75) continue;
            int smallerRadius = MathUtils.random(5, 12);
            GridPoint2 newCenter = new GridPoint2(smallerRadius, smallerRadius);
            offset.set(-center.x - newCenter.x + point.x, -center.y - newCenter.y + point.y);
            addResource(StandalonePoissonDisk.sampleCircle(newCenter, smallerRadius, 2F, 2 * smallerRadius, 2 * smallerRadius, 10, random), offset);
        }


    }

    private void addResource(OrderedMap<GridPoint2, Array<GridPoint2>> map, GridPoint2 offset) {
        HashedPoint chunkPoint = new HashedPoint();
        for (GridPoint2 point : map.keys()) {
            if (Math.random() < 0.2) continue;


            point.x += offset.x;
            point.y += offset.y;
            int x = point.x;
            int y = point.y;
            if (x < 0) chunkPoint.x = (x + 1) / chunkSize - 1;
            else chunkPoint.x = x / chunkSize;
            if (y < 0) chunkPoint.y = (y + 1) / chunkSize - 1;
            else chunkPoint.y = y / chunkSize;
            chunkPoint.recalculateHash();

            if (!resourcePoints.containsKey(chunkPoint)) {
                resourcePoints.put(new HashedPoint(chunkPoint), new Array<>());
            }
            resourcePoints.get(chunkPoint).add(point);
        }
    }

    @Override
    public ServerChunk loadChunk(HashedPoint point) {
        ServerChunk chunk = new ServerChunk(chunkSize, point);
        Tile[][] tiles = chunk.getTiles();
        int i, j;
        for (i = 0; i < chunkSize; i++) {
            for (j = 0; j < chunkSize; j++) {
                int x = i + point.x * chunkSize;
                int y = j + point.y * chunkSize;
                double value = (noise.eval(x / 100F, y / 100F, 0F) + 1)/ 2F; // Gives a value from 0 to 1
                int tileNum = (int)(value * 100);
                Tile tile = Tiles.getInstance().tileRegistry.get(tileNum);
                if(tile == null) {
                    Pixmap pixmap = new Pixmap(GameSettings.TILE_TEXTURE_SIZE, GameSettings.TILE_TEXTURE_SIZE, Pixmap.Format.RGBA8888);
                    tile = createNewTile(tileNum, pixmap);
                    pixmap.dispose();
                }
                tiles[i][j] = tile;
            }
        }

        loadedChunks.add(point);

        i = 0;
        if (resourcePoints.containsKey(point)) {
            Array<GridPoint2> resources = resourcePoints.remove(point);

            for (GridPoint2 rp : resources) {
                Stone stone = new Stone(rp.x, rp.y);
                i++;
                if (chunk.setTileEntity(stone)) {
                    chunk.addToItemsToAdd(stone);
                }
            }
        }


        return chunk;
    }

    private Tile createNewTile(int tileNum, Pixmap pixmap) {
        Tile tile = new Tile();
        Color baseColor = colors[MathUtils.random(colors.length - 1)];
        Color offsetColor = new Color();
        final float lowerMultiplier = 0.9F, upperMultiplier = 1.1F;
        for(int i = 0; i < GameSettings.TILE_TEXTURE_SIZE; i++) {
            for(int j = 0; j < GameSettings.TILE_TEXTURE_SIZE; j++) {
                offsetColor.set(baseColor);
                offsetColor.mul(MathUtils.random(lowerMultiplier, upperMultiplier), MathUtils.random(lowerMultiplier, upperMultiplier), MathUtils.random(lowerMultiplier, upperMultiplier), 1);
                pixmap.setColor(offsetColor);
                pixmap.drawPixel(i, j);
            }
        }
        Tiles.getInstance().register(tile, pixmap, tileNum);
        System.out.println("Registered new tile: " + tileNum);
        return tile;
    }


}
