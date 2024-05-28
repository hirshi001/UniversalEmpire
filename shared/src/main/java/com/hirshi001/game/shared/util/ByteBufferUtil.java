package com.hirshi001.game.shared.util;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.buffer.buffers.ByteBuffer;

public class ByteBufferUtil {

    public static void writeRange(ByteBuffer out, Range range) {
        out.writeFloat(range.min);
        out.writeFloat(range.max);
    }

    public static Range readRange(ByteBuffer in) {
        return new Range(in.readFloat(), in.readFloat());
    }

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

    public static void writeTextureRegion(ByteBuffer out, TextureRegion textureRegion) {
        out.writeInt(textureRegion.getRegionWidth());
        out.writeInt(textureRegion.getRegionHeight());
        Pixmap pixmap = textureRegion.getTexture().getTextureData().consumePixmap();
        for(int x = textureRegion.getRegionX(); x < textureRegion.getRegionX() + textureRegion.getRegionWidth(); x++){
            for(int y = textureRegion.getRegionY(); y < textureRegion.getRegionY() + textureRegion.getRegionHeight(); y++){
                out.writeInt(pixmap.getPixel(x, y));
            }
        }
    }

    public static Pixmap readTextureRegion(ByteBuffer in) {
        int width = in.readInt();
        int height = in.readInt();
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                pixmap.drawPixel(x, y, in.readInt());
            }
        }
        return pixmap;
    }

}
