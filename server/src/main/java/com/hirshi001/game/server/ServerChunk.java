package com.hirshi001.game.server;

import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.util.HashedPoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ServerChunk extends Chunk {

    public Set<PlayerData> trackers = Collections.synchronizedSet(new HashSet<>());
    public Set<PlayerData> softTrackers = Collections.synchronizedSet(new HashSet<>());

    public ServerChunk(int chunkSize, HashedPoint chunkPosition) {
        super(chunkSize, chunkPosition);
    }

    public ServerChunk(int chunkSize, HashedPoint chunkPosition, Tile[][] tiles) {
        super(chunkSize, chunkPosition, tiles);
    }
}
