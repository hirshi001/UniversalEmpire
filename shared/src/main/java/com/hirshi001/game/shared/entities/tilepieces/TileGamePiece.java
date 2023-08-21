package com.hirshi001.game.shared.entities.tilepieces;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.GamePiece;

public abstract class TileGamePiece extends GamePiece {

    public int x, y, width, height;

    public TileGamePiece() {
        super();
    }

    public TileGamePiece(int x, int y, int width, int height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.position.set(x + width / 2F, y + height / 2F);
    }


    @Override
    public void writeBytes(ByteBuffer buffer) {
        buffer.writeInt(getGameId());
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(width);
        buffer.writeInt(height);
        getProperties().writeBytes(buffer);
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        setGameId(buffer.readInt());
        x = buffer.readInt();
        y = buffer.readInt();
        width = buffer.readInt();
        height = buffer.readInt();
        this.position.set(x + width / 2F, y + height / 2F);
        getProperties().readBytes(buffer);
    }

    @Override
    public boolean needsSync() {
        return false;
    }
}
