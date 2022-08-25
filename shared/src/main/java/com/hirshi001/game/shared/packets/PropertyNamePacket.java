package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.util.ByteBufferUtil;
import com.hirshi001.game.shared.util.props.MissingClassException;
import com.hirshi001.game.shared.util.props.MissingSerializerException;
import com.hirshi001.networking.packet.Packet;

public class PropertyNamePacket extends Packet {

    public int gamePieceId, propertyId;
    public String propertyName;
    public Object value;

    public PropertyNamePacket() {
        super();
    }

    public PropertyNamePacket(int gamePieceId, int propertyId, String propertyName, Object value) {
        this.gamePieceId = gamePieceId;
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeInt(gamePieceId);
        out.writeInt(propertyId);
        ByteBufUtil.writeStringToBuf(propertyName, out);
        GameSettings.MANAGER.serialize(value, out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        gamePieceId = in.readInt();
        propertyId = in.readInt();
        propertyName = ByteBufUtil.readStringFromBuf(in);
        try {
            value = GameSettings.MANAGER.deserialize(in);
        } catch (MissingClassException | MissingSerializerException e) {
            e.printStackTrace();
        }
    }
}
