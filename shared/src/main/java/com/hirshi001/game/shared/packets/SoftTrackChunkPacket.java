package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;

public class SoftTrackChunkPacket extends TrackChunkPacket{

    public SoftTrackChunkPacket() {
        super();
    }

    public SoftTrackChunkPacket(int chunkX, int chunkY) {
        super(chunkX, chunkY);
    }

    public SoftTrackChunkPacket(int chunkX, int chunkY, boolean untrack) {
        super(chunkX, chunkY, untrack);
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
    }
}
