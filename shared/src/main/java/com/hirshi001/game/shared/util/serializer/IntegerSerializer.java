package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;

public class IntegerSerializer implements ByteBufSerializer<Integer> {

    @Override
    public void serialize(Integer object, ByteBuffer buffer) {
        buffer.writeInt(object);
    }

    @Override
    public Integer deserialize(ByteBuffer buffer) {
        return buffer.readInt();
    }

}

