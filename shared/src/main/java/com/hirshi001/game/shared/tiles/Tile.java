package com.hirshi001.game.shared.tiles;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.registry.ID;
import com.hirshi001.game.shared.util.ByteBufferUtil;
import com.hirshi001.game.shared.util.Range;
import com.hirshi001.game.shared.util.serializer.Serializers;
import com.hirshi001.networking.packet.ByteBufSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tile implements ID, ByteBufSerializable {

    private int id;

    public boolean isSolid;

    public Range temperature, humidity, height, plantGrowth;
    public TextureRegion texture;


    public Tile(){
        this(false);
    }

    public Tile(boolean isSolid){
        this.isSolid = isSolid;
        temperature = Range.largestRange();
        humidity = Range.largestRange();
        height = Range.largestRange();
        plantGrowth = Range.largestRange();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    public void onGamePieceMove(GamePiece gamePiece) {

    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        buffer.writeInt(id);
        buffer.writeBoolean(isSolid);
        ByteBufferUtil.writeRange(buffer, temperature);
        ByteBufferUtil.writeRange(buffer, humidity);
        ByteBufferUtil.writeRange(buffer, height);
        ByteBufferUtil.writeRange(buffer, plantGrowth);
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        id = buffer.readInt();
        isSolid = buffer.readBoolean();
        temperature = ByteBufferUtil.readRange(buffer);
        humidity = ByteBufferUtil.readRange(buffer);
        height = ByteBufferUtil.readRange(buffer);
        plantGrowth = ByteBufferUtil.readRange(buffer);
    }

    public void set(Tile other) {
        this.id = other.id;
        this.isSolid = other.isSolid;
        this.temperature = other.temperature;
        this.humidity = other.humidity;
        this.height = other.height;
        this.plantGrowth = other.plantGrowth;
    }
}
