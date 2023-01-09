package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.networking.packet.Packet;

public class ShootPacket extends Packet {

    public float angle;

    public ShootPacket() {
        super();
    }

    public ShootPacket(float angle){
        this.angle = angle;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeFloat(angle);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        angle = in.readFloat();
    }
}
