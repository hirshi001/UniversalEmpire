package com.hirshi001.game.server;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.hirshi001.game.shared.entities.Stone;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.game.shared.util.HashedPoint;

import java.util.*;

public class ServerChunkLoader implements ChunkLoader {

    OpenSimplexNoise noise = new OpenSimplexNoise(0);
    Random random = new Random();

    Set<HashedPoint> loadedChunks = new HashSet<>();
    Map<HashedPoint, Array<GridPoint2>> resourcePoints = new HashMap<>();
    private final int chunkSize;

    public ServerChunkLoader(int chunkSize) {
        this.chunkSize = chunkSize;


        int radius = 1024;
        GridPoint2 center = new GridPoint2(radius, radius);
        OrderedMap<GridPoint2, Array<GridPoint2>> resourcePointMap = StandalonePoissonDisk.sampleCircle(center, radius, 32, 2*radius, 2*radius);
        // OrderedMap<GridPoint2, Array<GridPoint2>> resourcePointMap = StandalonePoissonDisk.sampleRectangle(new GridPoint2(0, 0), new GridPoint2(2048, 2048), 10);

        resourcePointMap.removeIndex(0); // remove center point

        GridPoint2 offset = new GridPoint2();
        for(GridPoint2 point:resourcePointMap.keys()){
            if(Math.random()<0.75) continue;
            int smallerRadius = MathUtils.random(5, 12);
            GridPoint2 newCenter = new GridPoint2(smallerRadius, smallerRadius);
            offset.set(- center.x - newCenter.x + point.x, -center.y - newCenter.y + point.y);
            addResource(StandalonePoissonDisk.sampleCircle(newCenter, smallerRadius, 2F, 2*smallerRadius, 2*smallerRadius, 10, random), offset);
        }



    }

    private void addResource(OrderedMap<GridPoint2, Array<GridPoint2>> map, GridPoint2 offset){
        HashedPoint chunkPoint = new HashedPoint();
        for (GridPoint2 point : map.keys()) {
            if(Math.random()<0.2) continue;


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
                double random = Math.random();

                if (random < 0.1) tiles[i][j] = Tiles.STONE;
                else tiles[i][j] = Tiles.GRASS;
            }
        }

        loadedChunks.add(point);

        i = 0;
        if (resourcePoints.containsKey(point)) {
            Array<GridPoint2> resources = resourcePoints.remove(point);

            for (GridPoint2 rp : resources) {
                Stone stone = new Stone();
                i++;
                stone.bounds.setPosition(rp.x - stone.bounds.width / 2 + MathUtils.random(-0.3F, 0.3F), rp.y - stone.bounds.height / 2 + MathUtils.random(-0.3F, 0.3F));
                chunk.addToItemsToAdd(stone); // We can't use chunk.add because the hashCode of the Stone is equal to gameId which it currently does not have
            }
        }


        return chunk;
    }
}
