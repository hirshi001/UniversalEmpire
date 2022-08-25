package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;

public class FloatSerializer implements ByteBufSerializer<Float>{
    @Override
    public void serialize(Float object, ByteBuffer buffer) {
        buffer.writeFloat(object);
    }

    @Override
    public Float deserialize(ByteBuffer buffer) {
        return buffer.readFloat();
    }
}
