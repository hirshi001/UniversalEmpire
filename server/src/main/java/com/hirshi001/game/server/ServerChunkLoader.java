package com.hirshi001.game.server;

import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.game.shared.util.HashedPoint;

public class ServerChunkLoader implements ChunkLoader{
    @Override
    public ServerChunk loadChunk(int chunkSize, HashedPoint point) {
        ServerChunk chunk = new ServerChunk(chunkSize, point);
        Tile[][] tiles = chunk.getTiles();
        int i, j;
        for(i = 0; i < chunkSize; i++){
            for(j = 0; j < chunkSize; j++){
                double random = Math.random();
                if(random < 0.1) tiles[i][j] = Tiles.STONE;
                else tiles[i][j] = Tiles.GRASS;
            }
        }
        return chunk;
    }
}
