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

    public final Rectangle bounds;
    public Field field;
    public Chunk chunk;
    private int gameId;
    private int id;
    public long lastTickUpdate;
    public boolean alive;
    public boolean softLoaded = false;
    private final Properties properties = GameSettings.MANAGER.createNewProps();


    public static final CollisionFilter DEFAULT_COLLISION_FILTER = (item, other) -> {
        GamePiece gamePiece = (GamePiece) item;
        GamePiece otherGamePiece = (GamePiece) other;
        if (!gamePiece.worldInteractable() || !otherGamePiece.worldInteractable()) {
            return null;
        }
        if (gamePiece.isStatic() || otherGamePiece.isStatic()) {
            return Response.slide;
        }
        return Response.cross;
    };


    public GamePiece() {
        bounds = new Rectangle();
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

    public float getCenterX() {
        return bounds.x + bounds.width / 2;
    }

    public float getCenterY() {
        return bounds.y + bounds.height / 2;
    }

    public abstract boolean isStatic();

    public boolean worldInteractable() {
        return true;
    }

    public boolean isProjectile() {
        return false;
    }

    public boolean isLivingEntity() {
        return false;
    }

    public CollisionFilter getCollisionFilter() {
        return DEFAULT_COLLISION_FILTER;
    }

    public void update() {
        if (worldInteractable() && field != null) field.update(this, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean collides() {
        return true;
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        buffer.writeInt(getGameId());
        ByteBufferUtil.writeRectangle(buffer, bounds);
        properties.writeBytes(buffer);
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        setGameId(buffer.readInt());
        ByteBufferUtil.readRectangle(buffer, bounds);
        properties.readBytes(buffer);
    }

    public void writeSyncBytes(ByteBuffer buffer) {
        buffer.writeFloat(bounds.x);
        buffer.writeFloat(bounds.y);
    }

    public void readSyncBytes(ByteBuffer buffer) {
        bounds.x = buffer.readFloat();
        bounds.y = buffer.readFloat();
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
