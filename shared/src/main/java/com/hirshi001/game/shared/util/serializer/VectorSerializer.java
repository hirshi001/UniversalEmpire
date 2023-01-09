package com.hirshi001.game.shared.util.serializer;

import com.badlogic.gdx.math.Vector2;
import com.hirshi001.buffer.buffers.ByteBuffer;

public class VectorSerializer implements ByteBufSerializer<Vector2> {
    @Override
    public void serialize(Vector2 object, ByteBuffer buffer) {
        buffer.writeFloat(object.x);
        buffer.writeFloat(object.y);
    }

    @Override
    public Vector2 deserialize(ByteBuffer buffer) {
        return new Vector2(buffer.readFloat(), buffer.readFloat());
    }
}
