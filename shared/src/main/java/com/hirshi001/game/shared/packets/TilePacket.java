package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.networking.packet.Packet;

public class TilePacket extends Packet {

    public Tile tile;
    public byte[] texture;


    public TilePacket(){

    }

    public TilePacket(Tile tile, byte[] texture){
        this.tile = tile;
        this.texture = texture;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        tile.writeBytes(out);
        out.writeInt(texture.length);
        out.writeBytes(texture);

    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        tile = new Tile();
        tile.readBytes(in);

        int length = in.readInt();
        texture = new byte[length];
        in.readBytes(texture);
    }
}
