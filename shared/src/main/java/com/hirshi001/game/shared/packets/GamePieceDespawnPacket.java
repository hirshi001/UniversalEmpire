package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.networking.packet.Packet;

public class GamePieceDespawnPacket extends Packet {

    public int gamePieceID;

    public GamePieceDespawnPacket() {
        super();
    }

    public GamePieceDespawnPacket(int gamePieceID) {
        super();
        this.gamePieceID = gamePieceID;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeInt(gamePieceID);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        gamePieceID = in.readInt();
    }
}
