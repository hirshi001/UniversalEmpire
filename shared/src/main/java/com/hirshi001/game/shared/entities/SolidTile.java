package com.hirshi001.game.shared.entities;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.tilepieces.TileGamePiece;

public class SolidTile extends TileGamePiece {


    public SolidTile(){

    }
    public SolidTile(int x, int y) {
        super(x, y, 1, 1);
    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {
        // super.writeSyncBytes(buffer);
    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {
        // super.readSyncBytes(buffer);
    }


}
