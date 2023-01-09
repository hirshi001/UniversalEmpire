package com.hirshi001.game.shared.packets;

import com.hirshi001.networking.packet.Packet;

public class PingPacket extends Packet {

    public long time;

    public PingPacket() {
        super();
    }

    public PingPacket(long time) {
        super();
        this.time = time;
    }

    @Override
    public void writeBytes(com.hirshi001.buffer.buffers.ByteBuffer out) {
        super.writeBytes(out);
        out.ensureWritable(8);
        out.writeLong(time);
    }

    @Override
    public void readBytes(com.hirshi001.buffer.buffers.ByteBuffer in) {
        super.readBytes(in);
        time = in.readLong();
    }
}
