package com.hirshi001.game.shared.util.serializer;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.util.ByteBufferUtil;

public class PathSerializer implements ByteBufSerializer<LinePath> {
    @Override
    public void serialize(LinePath object, ByteBuffer buffer) {
        ByteBufferUtil.writePath(buffer, object);
    }

    @Override
    public LinePath<Vector2> deserialize(ByteBuffer buffer) {
        return ByteBufferUtil.readPath(buffer);
    }
}
