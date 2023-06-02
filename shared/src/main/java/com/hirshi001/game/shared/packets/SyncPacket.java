package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.networking.packet.Packet;

public class SyncPacket extends Packet {

    public long time;
    public int id;
    public int size;
    public ByteBuffer buffer;
    public GamePiece gamePiece;

    public SyncPacket() {
        super();
    }

    public SyncPacket(long time, GamePiece gamePiece){
        super();
        this.time = time;
        this.id = gamePiece.getGameId();
        this.gamePiece = gamePiece;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeLong(time);
        out.writeInt(id);
        gamePiece.writeSyncBytes(out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        time = in.readLong();
        id = in.readInt();
        buffer = in.readBytes(in.readableBytes());
    }
}
