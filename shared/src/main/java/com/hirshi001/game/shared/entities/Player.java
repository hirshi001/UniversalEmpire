package com.hirshi001.game.shared.entities;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.util.props.Properties;

import java.util.concurrent.ThreadLocalRandom;

public class Player extends Entity {

    Vector2 temp = new Vector2();

    protected LinePath.LinePathParam pathParam = new LinePath.LinePathParam();

    public Player() {
    }

    @Override
    public void setField(Field field) {
        super.setField(field);
        bounds.setSize(0.8F, 0.8F);
        update();
        if(field.isServer()) {
            CircleGamePiece circleGamePiece = new CircleGamePiece(getGameId());
            field.addGamePiece(circleGamePiece);
            Properties properties = getProperties();
            properties.put("path", new LinePath<Vector2>(new Array<>() {{
                add(new Vector2(-10, 5));
                add(new Vector2(10, 5));
            }}, false));
            properties.put("circleId", circleGamePiece.getGameId());
            properties.put("health", 100);
        }
    }

    float time;

    @Override
    public void tick(float delta) {
        super.tick(delta);
        LinePath<Vector2> path = getProperties().get("path");
        Number s = getProperties().get("speed", 20F);
        float speed = s.floatValue();
        if(path!=null) {
            float distance = pathParam.getDistance() + speed * delta;
            path.calculateTargetPosition(temp, pathParam, distance);
            pathParam.setDistance(distance);
            if (path.isOpen() && path.getEndPoint().epsilonEquals(temp, 0.0001F)) {
                bounds.setPosition(path.getEndPoint());
                path=null;
            } else bounds.setPosition(temp);
            update();
        }


        if(field.isServer()) {

            time += delta;
            if (time > 5F || path==null) {
                int dist = GameSettings.CHUNK_SIZE * 5;
                getProperties().put("path", new LinePath<Vector2>(new Array<>() {{
                    add(new Vector2(bounds.x, bounds.y));
                    add(new Vector2(ThreadLocalRandom.current().nextFloat()*2*dist-dist, ThreadLocalRandom.current().nextFloat()*2*dist-dist));
                }}, true));
                pathParam.setDistance(0F);
                time = 0;
            }
        }
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        super.writeBytes(buffer);
        buffer.writeFloat(pathParam.getDistance());
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        super.readBytes(buffer);
        pathParam.setDistance(buffer.readFloat());
    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {
        super.writeSyncBytes(buffer);
        buffer.writeFloat(pathParam.getDistance());
    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {
        super.readSyncBytes(buffer);
        pathParam.setDistance(buffer.readFloat());
    }
}
