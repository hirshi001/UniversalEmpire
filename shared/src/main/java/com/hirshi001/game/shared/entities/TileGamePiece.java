package com.hirshi001.game.shared.entities;

import com.badlogic.gdx.math.MathUtils;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.tiles.Tile;

public class TileGamePiece extends GamePiece {

    private int x, y;

    public TileGamePiece(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }


    @Override
    public void setField(Field field) {
        super.setField(field);
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        super.writeBytes(buffer);
        buffer.writeInt(x);
        buffer.writeInt(y);
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        super.readBytes(buffer);
        x = buffer.readInt();
        y = buffer.readInt();
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
    }

    @Override
    public boolean isStatic() {
        return true;
    }
}
