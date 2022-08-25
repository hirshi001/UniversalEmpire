package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.networking.packet.Packet;

public class TrackChunkPacket extends Packet {

    public int chunkX, chunkY;
    public boolean untrack;


    public TrackChunkPacket() {
    }

    public TrackChunkPacket(int chunkX, int chunkY) {
        this(chunkX, chunkY, false);
    }

    public TrackChunkPacket(int chunkX, int chunkY, boolean untrack){
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.untrack = untrack;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        out.writeInt(chunkX);
        out.writeInt(chunkY);
        out.writeBoolean(untrack);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        chunkX = in.readInt();
        chunkY = in.readInt();
        untrack = in.readBoolean();
    }
}
