package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.networking.packet.Packet;

public class SyncPacket extends Packet {

    public long tick;
    public int id;
    public int size;
    public ByteBuffer buffer;
    public GamePiece gamePiece;

    public SyncPacket() {
        super();
    }

    public SyncPacket(long tick, GamePiece gamePiece){
        super();
        this.tick = tick;
        this.id = gamePiece.getGameId();
        this.gamePiece = gamePiece;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeLong(tick);
        out.writeInt(id);
        gamePiece.writeSyncBytes(out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        tick = in.readLong();
        id = in.readInt();
        buffer = in.readBytes(in.readableBytes());
    }
}
