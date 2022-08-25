package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;

public interface ByteBufSerializer<T>{

    public void serialize(T object, ByteBuffer buffer);

    public T deserialize(ByteBuffer buffer);
}
