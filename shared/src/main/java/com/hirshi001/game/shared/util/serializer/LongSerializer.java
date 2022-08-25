package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;

public class LongSerializer implements ByteBufSerializer<Long> {
    @Override
    public void serialize(Long object, ByteBuffer buffer) {
        buffer.writeLong(object);
    }

    @Override
    public Long deserialize(ByteBuffer buffer) {
        return buffer.readLong();
    }

}
