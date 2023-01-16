package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.util.ByteBufferUtil;
import com.hirshi001.networking.packet.Packet;

public class PlayerMovePacket extends Packet {

    public float newX, newY;
    public boolean forceMove = false;
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

    public PlayerMovePacket(float newX, float newY, int id, boolean forceMove, long tick) {
        super();
        this.newX = newX;
        this.newY = newY;
        this.id = id;
        this.forceMove = forceMove;
        this.tick = tick;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.ensureWritable(24);
        out.writeFloat(newX);
        out.writeFloat(newY);
        out.writeInt(id);
        out.writeLong(tick);
        out.writeBoolean(forceMove);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        this.newX = in.readFloat();
        this.newY = in.readFloat();
        id = in.readInt();
        tick = in.readLong();
        forceMove = in.readBoolean();
    }
}
