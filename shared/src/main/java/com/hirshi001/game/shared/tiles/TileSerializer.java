package com.hirshi001.game.shared.tiles;

import com.hirshi001.betternetworkingutil.ByteBufSerializer;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.util.ByteBufferUtil;

public class TileSerializer implements ByteBufSerializer<Tile> {
    @Override
    public void serialize(Tile tile, ByteBuffer buffer) {
        buffer.writeInt(tile.getID());
        buffer.writeBoolean(tile.isSolid);
        ByteBufferUtil.writeRange(buffer, tile.temperature);
        ByteBufferUtil.writeRange(buffer, tile.humidity);
        ByteBufferUtil.writeRange(buffer, tile.height);
        ByteBufferUtil.writeRange(buffer, tile.plantGrowth);
    }

    @Override
    public Tile deserialize(ByteBuffer buffer) {
        Tile tile = new Tile();
        tile.setID(buffer.readInt());
        tile.isSolid = buffer.readBoolean();
        tile.temperature = ByteBufferUtil.readRange(buffer);
        tile.humidity = ByteBufferUtil.readRange(buffer);
        tile.height = ByteBufferUtil.readRange(buffer);
        tile.plantGrowth = ByteBufferUtil.readRange(buffer);
        return tile;
    }
}
