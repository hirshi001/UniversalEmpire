package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.networking.packet.Packet;

public class RequestTilePacket extends Packet {

    public int id;

    public RequestTilePacket(int id){
        this.id = id;
    }

    public RequestTilePacket(){

    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        ByteBufUtil.writeVarInt(out, id);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        id = ByteBufUtil.readVarInt(in);
    }
}
