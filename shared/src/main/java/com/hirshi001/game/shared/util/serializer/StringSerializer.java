package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;

public class StringSerializer implements ByteBufSerializer<String> {
    @Override
    public void serialize(String object, ByteBuffer buffer) {
        ByteBufUtil.writeStringToBuf(object, buffer);
    }

    @Override
    public String deserialize(ByteBuffer buffer) {
        return ByteBufUtil.readStringFromBuf(buffer);
    }
}
