package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;

public class DoubleSerializer implements ByteBufSerializer<Double>{
    @Override
    public void serialize(Double object, ByteBuffer buffer) {
        buffer.writeDouble(object);
    }

    @Override
    public Double deserialize(ByteBuffer buffer) {
        return buffer.readDouble();
    }

}
