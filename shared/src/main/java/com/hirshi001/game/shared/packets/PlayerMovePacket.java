package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.util.ByteBufferUtil;
import com.hirshi001.networking.packet.Packet;

public class PlayerMovePacket extends Packet {

    public float newX, newY;
    public int id;
    public long tick;

    public PlayerMovePacket() {
        super();
    }

    public PlayerMovePacket(float newX, float newY, int id, long tick) {
        super();
        this.newX = newX;
        this.newY = newY;
        this.id = id;
        this.tick = tick;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.ensureWritable(20);
        out.writeFloat(newX);
        out.writeFloat(newY);
        out.writeInt(id);
        out.writeLong(tick);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        this.newX = in.readFloat();
        this.newY = in.readFloat();
        id = in.readInt();
        tick = in.readLong();
    }
}
