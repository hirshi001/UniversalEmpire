package com.hirshi001.game.shared.util.stringutils;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;

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

    public String toString(T object);

}
