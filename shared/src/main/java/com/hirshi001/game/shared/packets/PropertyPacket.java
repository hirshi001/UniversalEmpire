package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.util.props.MissingClassException;
import com.hirshi001.game.shared.util.props.MissingSerializerException;
import com.hirshi001.networking.packet.Packet;

public class PropertyPacket extends Packet {

    public int gamePieceId;
    public int propertyId;
    public Object value;

    public PropertyPacket() {
        super();
    }

    public PropertyPacket(int gamePieceId, int propertyId, Object value){
        this.gamePieceId = gamePieceId;
        this.propertyId = propertyId;
        this.value = value;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeInt(gamePieceId);
        out.writeInt(propertyId);
        GameSettings.MANAGER.serialize(value, out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        gamePieceId = in.readInt();
        propertyId = in.readInt();
        try {
            value = GameSettings.MANAGER.deserialize(in);
        } catch (MissingClassException | MissingSerializerException e) {
            e.printStackTrace();
        }
    }
}
