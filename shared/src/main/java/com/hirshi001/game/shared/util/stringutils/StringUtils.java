package com.hirshi001.game.shared.util.stringutils;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;

import java.util.HashMap;
import java.util.Map;

public class StringUtils {

    private static final StringUtils instance = new StringUtils();

    public static StringUtils DEFAULT(){
        return instance;
    }

    static{
        instance.register(LinePath.class, ObjectToString.LINE_PATH);
    }

    public Map<Class, ObjectToString> objectToStringMap = new HashMap<>();

    public String toString(Object object) {
        if(objectToStringMap.containsKey(object.getClass())){
            return objectToStringMap.get(object.getClass()).toString(object);
        }
        return object.toString();
    }

    public <T> void register(Class<T> clazz, ObjectToString<T> toString){
        objectToStringMap.put(clazz, toString);
    }


}
