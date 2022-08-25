package com.hirshi001.game.shared.util;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.buffer.buffers.ByteBuffer;

public class ByteBufferUtil {

    public static void writeRectangle(ByteBuffer out, Rectangle rectangle) {
        out.writeFloat(rectangle.x);
        out.writeFloat(rectangle.y);
        out.writeFloat(rectangle.width);
        out.writeFloat(rectangle.height);
    }

    public static void readRectangle(ByteBuffer in, Rectangle rectangle) {
        rectangle.x = in.readFloat();
        rectangle.y = in.readFloat();
        rectangle.width = in.readFloat();
        rectangle.height = in.readFloat();
    }

    public static void writeVector2(ByteBuffer out, Vector2 vector) {
        out.writeFloat(vector.x);
        out.writeFloat(vector.y);
    }

    public static void readVector2(ByteBuffer in, Vector2 vector) {
        vector.x = in.readFloat();
        vector.y = in.readFloat();
    }

    public static void writePath(ByteBuffer out, LinePath<Vector2> path) {
        out.writeBoolean(path.isOpen());
        out.writeInt(path.getSegments().size);
        for(LinePath.Segment<Vector2> segment:path.getSegments()){
            writeVector2(out, segment.getBegin());
        }
        writeVector2(out, path.getEndPoint());
    }

    public static LinePath<Vector2> readPath(ByteBuffer in) {
        boolean open = in.readBoolean();
        int size = in.readInt();
        Array<Vector2> path = new Array<>(size+1);
        for(int i=0;i<size+1;i++){
            Vector2 waypoint = new Vector2();
            readVector2(in, waypoint);
            path.add(waypoint);
        }
        return new LinePath<>(path, open);
    }

}
