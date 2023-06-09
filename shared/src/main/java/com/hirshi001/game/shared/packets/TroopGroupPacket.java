package com.hirshi001.game.shared.packets;

import com.badlogic.gdx.utils.Array;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.networking.packet.Packet;

public class TroopGroupPacket extends Packet {

    public enum OperationType {
        CREATE, ADD, REMOVE, DELETE
    }

    public OperationType type;
    public String name;
    public int[] troopIds;

    public TroopGroupPacket(){}

    public TroopGroupPacket(OperationType type, String name, Array<Integer> troops){
        this.type = type;
        this.name = name;
        if(troops!=null) {
            this.troopIds = new int[troops.size];
            for (int i = 0; i < troops.size; i++) {
                troopIds[i] = troops.get(i);
            }
        }
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeByte(type.ordinal());
        ByteBufUtil.writeStringToBuf(name, out);
        if(type==OperationType.DELETE) return;
        out.writeByte(troopIds.length);
        for(int i : troopIds){
            out.writeInt(i);
        }
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        type = OperationType.values()[in.readByte()];
        name = ByteBufUtil.readStringFromBuf(in);
        if(type==OperationType.DELETE) return;
        troopIds = new int[in.readByte()];
        for(int i = 0; i < troopIds.length; i++){
            troopIds[i] = in.readInt();
        }
    }
}
