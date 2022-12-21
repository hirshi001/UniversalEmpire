package com.hirshi001.game.shared.entities;

import com.badlogic.gdx.math.MathUtils;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GamePiece;

public class CircleGamePiece extends GamePiece {

    private int idOwner;
    GamePiece owner;
    float deltaTime;
    float radius = 3F;

    public CircleGamePiece() {
        super();
    }

    public CircleGamePiece(int idOwner){
        this.idOwner = idOwner;
    }

    @Override
    public void setField(Field field) {
        super.setField(field);
        bounds.width=1F;
        bounds.height=1F;
        update();
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        this.deltaTime += delta;
        radius = deltaTime/10F;
        if(owner==null || !owner.alive){
            owner=field.getGamePiece(idOwner);
        }

        if(owner!=null){
            bounds.setPosition(owner.getCenterX() + radius*MathUtils.cos(deltaTime), owner.getCenterY() + radius*MathUtils.sin(deltaTime));
        }
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        super.writeBytes(buffer);
        buffer.writeInt(idOwner);
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        super.readBytes(buffer);
        idOwner = buffer.readInt();
    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {
        super.writeSyncBytes(buffer);
        buffer.writeFloat(deltaTime);
    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {
        super.readSyncBytes(buffer);
        deltaTime = buffer.readFloat();
    }
}
