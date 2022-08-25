package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.networking.packet.Packet;

public class GameInitPacket extends Packet {

    //public int playerId;

    public GameInitPacket() {
        super();
    }

    public GameInitPacket(int playerId){
        super();
        //this.playerId = playerId;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        //out.writeInt(playerId);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        //playerId = in.readInt();
    }
}
