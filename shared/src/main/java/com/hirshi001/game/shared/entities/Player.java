package com.hirshi001.game.shared.entities;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.util.props.Properties;

import java.awt.geom.Path2D;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class Player extends Entity {

    Vector2 temp = new Vector2();

    protected LinePath.LinePathParam pathParam;

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
                add(new Vector2(0, 0));
                add(new Vector2(0, 16));
                add(new Vector2(16, 16));
                add(new Vector2(16, 0));
            }}, false));
            properties.put("circleId", circleGamePiece.getGameId());
            properties.put("health", 100);
        }
        pathParam = new LinePath.LinePathParam();
    }

    float time;

    @Override
    public void tick(float delta) {
        super.tick(delta);
        LinePath<Vector2> path = getProperties().get("path");
        if(path!=null) {
            path.calculateTargetPosition(temp, pathParam, pathParam.getDistance() + 5*delta);
            pathParam.setDistance(pathParam.getDistance() + 5*delta);
            if (path.getEndPoint().epsilonEquals(temp, 0.0001F) && path.isOpen()) {
                bounds.setPosition(path.getEndPoint());
                path=null;
            } else bounds.setPosition(temp);
            update();
        }

        if(field.isServer()) {
            time += delta;
            if (time > 5F || path==null) {
                getProperties().put("path", new LinePath<Vector2>(new Array<>() {{
                    add(new Vector2(bounds.x, bounds.y));
                    add(new Vector2(ThreadLocalRandom.current().nextFloat()*32-16, ThreadLocalRandom.current().nextFloat()*32-16));
                }}, true));
                pathParam.setDistance(0F);
                time = 0;
            }
        }
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
