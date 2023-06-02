package com.hirshi001.game.shared.util.stringutils;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.hirshi001.game.shared.control.MoveTroopMovement;
import com.hirshi001.game.shared.control.TroopGroup;

@FunctionalInterface
public interface ObjectToString<T> {

    ObjectToString<LinePath> LINE_PATH = (object) -> {
        StringBuilder sb = new StringBuilder();
        sb.append("LinePath{");

        for(Object segment: object.getSegments()){
            sb.append(((LinePath.Segment)segment).getBegin().toString()).append(", ");
        }
        sb.append(object.getEndPoint().toString()).append("}");
        return sb.toString();
    };

    ObjectToString<TroopGroup> SIMPLE_TROOP_GROUP = (object) -> object.name;

    ObjectToString<MoveTroopMovement> MOVE_TROOP_MOVEMENT = (object) -> {
        StringBuilder sb = new StringBuilder();
        sb.append("MoveTroopMovement{");
        sb.append("x: ").append(object.x).append(", ");
        sb.append("y: ").append(object.y).append(", ");
        sb.append("radius2: ").append(object.radius2).append("}");
        return sb.toString();
    };

    String toString(T object);

}
