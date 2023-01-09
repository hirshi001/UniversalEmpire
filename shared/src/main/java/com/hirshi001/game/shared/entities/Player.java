package com.hirshi001.game.shared.entities;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.CollisionFilter;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.util.ByteBufferUtil;
import com.hirshi001.game.shared.util.props.Properties;
import com.hirshi001.game.shared.util.props.PropertiesManager;

import java.util.concurrent.ThreadLocalRandom;

public class Player extends Entity {

    Vector2 temp = new Vector2();

    public Player() {
    }

    @Override
    public void setField(Field field) {
        super.setField(field);
        bounds.setSize(0.8F, 0.8F);
        if (field.isServer()) {
            Properties props = getProperties();
            props.put("speed", 12F);
            props.put("health", 100F);
        }
        update();
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        if (field.isServer()) {
            Properties props = getProperties();
            if (props.get("health", 100F) <= 0) {
                bounds.x = 0;
                bounds.y = 0;
                props.put("health", 100F);
            }
        }
        update();
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        super.writeBytes(buffer);
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        super.readBytes(buffer);
    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {
        super.writeSyncBytes(buffer);
    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {
        super.readSyncBytes(buffer);
    }

    @Override
    public boolean shouldLoadChunk() {
        return true;
    }
}
