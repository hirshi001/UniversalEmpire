package com.hirshi001.game.server;

import com.hirshi001.game.shared.util.HashedPoint;

public interface ChunkLoader {

    public ServerChunk loadChunk(HashedPoint point);

}
