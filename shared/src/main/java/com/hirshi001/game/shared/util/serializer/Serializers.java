package com.hirshi001.game.shared.util.serializer;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.game.shared.util.ByteBufferUtil;
import com.hirshi001.game.shared.util.Range;

public class Serializers {

    public static final ByteBufSerializer<Byte> BYTE = new ByteBufSerializer<Byte>() {
        @Override
        public void serialize(Byte object, ByteBuffer buffer) {
            buffer.writeFloat(object);
        }

        @Override
        public Byte deserialize(ByteBuffer buffer) {
            return buffer.readByte();
        }
    };

    public static final ByteBufSerializer<Short> SHORT = new ByteBufSerializer<Short>() {
        @Override
        public void serialize(Short object, ByteBuffer buffer) {
            buffer.writeShort(object);
        }

        @Override
        public Short deserialize(ByteBuffer buffer) {
            return buffer.readShort();
        }
    };

    public static final ByteBufSerializer<Integer> INTEGER = new ByteBufSerializer<Integer>() {
        @Override
        public void serialize(Integer object, ByteBuffer buffer) {
            buffer.writeInt(object);
        }

        @Override
        public Integer deserialize(ByteBuffer buffer) {
            return buffer.readInt();
        }
    };

    public static final ByteBufSerializer<Long> LONG = new ByteBufSerializer<Long>() {
        @Override
        public void serialize(Long object, ByteBuffer buffer) {
            buffer.writeLong(object);
        }

        @Override
        public Long deserialize(ByteBuffer buffer) {
            return buffer.readLong();
        }
    };

    public static final ByteBufSerializer<Float> FLOAT = new ByteBufSerializer<Float>() {
        @Override
        public void serialize(Float object, ByteBuffer buffer) {
            buffer.writeFloat(object);
        }

        @Override
        public Float deserialize(ByteBuffer buffer) {
            return buffer.readFloat();
        }
    };

    public static final ByteBufSerializer<Double> DOUBLE = new ByteBufSerializer<Double>() {
        @Override
        public void serialize(Double object, ByteBuffer buffer) {
            buffer.writeDouble(object);
        }

        @Override
        public Double deserialize(ByteBuffer buffer) {
            return buffer.readDouble();
        }
    };

    public static final ByteBufSerializer<Boolean> BOOLEAN = new ByteBufSerializer<Boolean>() {
        @Override
        public void serialize(Boolean object, ByteBuffer buffer) {
            buffer.writeBoolean(object);
        }

        @Override
        public Boolean deserialize(ByteBuffer buffer) {
            return buffer.readBoolean();
        }
    };

    public static final ByteBufSerializer<Character> CHARACTER = new ByteBufSerializer<Character>() {
        @Override
        public void serialize(Character object, ByteBuffer buffer) {
            buffer.writeChar(object);
        }

        @Override
        public Character deserialize(ByteBuffer buffer) {
            return (char) buffer.readChar();
        }
    };

    public static final ByteBufSerializer<String> STRING = new ByteBufSerializer<String>() {
        @Override
        public void serialize(String object, ByteBuffer buffer) {
            ByteBufUtil.writeStringToBuf(object, buffer);
        }

        @Override
        public String deserialize(ByteBuffer buffer) {
            return ByteBufUtil.readStringFromBuf(buffer);
        }
    };

    public static final ByteBufSerializer<Vector2> VECTOR2 = new ByteBufSerializer<Vector2>() {
        @Override
        public void serialize(Vector2 object, ByteBuffer buffer) {
            buffer.writeFloat(object.x);
            buffer.writeFloat(object.y);
        }

        @Override
        public Vector2 deserialize(ByteBuffer buffer) {
            return new Vector2(buffer.readFloat(), buffer.readFloat());
        }
    };

    public static final ByteBufSerializer<Range> RANGE = new ByteBufSerializer<Range>() {
        @Override
        public void serialize(Range object, ByteBuffer buffer) {
            ByteBufferUtil.writeRange(buffer, object);
        }

        @Override
        public Range deserialize(ByteBuffer buffer) {
            return ByteBufferUtil.readRange(buffer);
        }
    };

    public static final ByteBufSerializer<LinePath> PATH = new ByteBufSerializer<LinePath>() {
        @Override
        public void serialize(LinePath object, ByteBuffer buffer) {
            ByteBufferUtil.writePath(buffer, object);
        }

        @Override
        public LinePath deserialize(ByteBuffer buffer) {
            return ByteBufferUtil.readPath(buffer);
        }
    };

}
