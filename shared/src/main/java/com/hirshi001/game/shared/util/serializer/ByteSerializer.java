package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;

public class ByteSerializer implements ByteBufSerializer<Byte> {

    @Override
    public void serialize(Byte object, ByteBuffer buffer) {
        buffer.writeByte(object);
    }

    @Override
    public Byte deserialize(ByteBuffer buffer) {
        return buffer.readByte();
    }


}
