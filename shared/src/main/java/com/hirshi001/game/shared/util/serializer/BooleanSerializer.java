package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;

public class BooleanSerializer implements ByteBufSerializer<Boolean>{
    @Override
    public void serialize(Boolean object, ByteBuffer buffer) {
        buffer.writeBoolean(object);
    }

    @Override
    public Boolean deserialize(ByteBuffer buffer) {
        return buffer.readBoolean();
    }
}
