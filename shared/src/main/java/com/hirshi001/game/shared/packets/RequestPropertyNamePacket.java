package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.networking.packet.Packet;

public class RequestPropertyNamePacket extends Packet {

    public int gamePieceId, propertyId;

    public RequestPropertyNamePacket() {
        super();
    }

    public RequestPropertyNamePacket(int gamePieceId, int propertyId) {
        this.gamePieceId = gamePieceId;
        this.propertyId = propertyId;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeInt(gamePieceId);
        out.writeInt(propertyId);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        gamePieceId = in.readInt();
        propertyId = in.readInt();
    }
}
