package com.hirshi001.game.shared.util.stringutils;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.hirshi001.game.shared.control.MoveTroopMovement;
import com.hirshi001.game.shared.control.TroopGroup;

import java.util.HashMap;
import java.util.Map;

public class StringUtils {

    private static final StringUtils instance = new StringUtils();

    public static StringUtils DEFAULT() {
        return instance;
    }

    static {
        instance.register(LinePath.class, ObjectToString.LINE_PATH);
        instance.register(TroopGroup.class, ObjectToString.SIMPLE_TROOP_GROUP);
        instance.register(MoveTroopMovement.class, ObjectToString.MOVE_TROOP_MOVEMENT);
    }

    public Map<Class, ObjectToString> objectToStringMap = new HashMap<>();

    public String toString(Object object) {
        if (object == null) return "null";
        try {
            if (objectToStringMap.containsKey(object.getClass())) {
                return objectToStringMap.get(object.getClass()).toString(object);
            }
        } catch (Exception ignored) {
        }
        return object.toString();
    }

    public <T> void register(Class<T> clazz, ObjectToString<T> toString) {
        objectToStringMap.put(clazz, toString);
    }


}
