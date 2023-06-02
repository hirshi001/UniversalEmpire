package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.networking.packet.Packet;

public class GameInitPacket extends Packet {

    public int playerControllerId;

    public GameInitPacket() {
        super();
    }

    public GameInitPacket(int playerControllerId){
        super();
        this.playerControllerId = playerControllerId;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeInt(playerControllerId);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        playerControllerId = in.readInt();
    }
}
