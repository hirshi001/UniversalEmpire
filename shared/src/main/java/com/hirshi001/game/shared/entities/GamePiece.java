package com.hirshi001.game.shared.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.registry.ID;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.util.ByteBufferUtil;
import com.hirshi001.game.shared.util.props.Properties;
import com.hirshi001.networking.packet.ByteBufSerializable;

public abstract class GamePiece extends Item implements ID, ByteBufSerializable {

    public Vector2 position;
    public Field field;
    public Chunk chunk;
    private int gameId;
    private int id;
    public long lastTickUpdate;
    public boolean alive;
    public boolean softLoaded = false;
    private final Properties properties = GameSettings.MANAGER.createNewProps();
    public boolean syncedRecently = false;

    public GamePiece() {
        position = new Vector2();
        setID(GamePieces.getId(this));
    }

    public void tick(float delta) {
    }


    public void setField(Field field) {
        this.field = field;
        this.alive = true;
    }

    public void removed() {
        this.alive = false;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setY(float y) {
        this.position.y = y;
    }

    public void setX(float x) {
        this.position.x = x;
    }

    public boolean worldInteractable() {
        return true;
    }

    public boolean isProjectile() {
        return false;
    }

    public boolean isLivingEntity() {
        return false;
    }

    public void update() {
    }

    public boolean collides() {
        return true;
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        buffer.writeInt(getGameId());
        ByteBufferUtil.writeVector2(buffer, position);
        properties.writeBytes(buffer);
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        setGameId(buffer.readInt());
        ByteBufferUtil.readVector2(buffer, position);
        properties.readBytes(buffer);
    }

    public void writeSyncBytes(ByteBuffer buffer) {
        ByteBufferUtil.writeVector2(buffer, position);
    }

    public void readSyncBytes(ByteBuffer buffer) {
        ByteBufferUtil.readVector2(buffer, position);
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean shouldLoadChunk() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GamePiece && ((GamePiece) o).getGameId() == getGameId();
    }

    @Override
    public int hashCode() {
        return getGameId();
    }

    public boolean needsSync() {
        return false;
    }
}
