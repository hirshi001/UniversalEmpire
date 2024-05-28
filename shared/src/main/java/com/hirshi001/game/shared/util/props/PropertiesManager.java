package com.hirshi001.game.shared.util.props;

import com.hirshi001.betternetworkingutil.ByteBufSerializer;
import com.hirshi001.buffer.buffers.ByteBuffer;

import java.util.HashMap;
import java.util.Map;

public class PropertiesManager {

    Map<Class, ByteBufSerializer> serializerMap = new HashMap<>();
    Map<Class, Integer> classIdMap = new HashMap<>();
    Map<Integer, Class> idClassMap = new HashMap<>();

    public <T> void register(Class<T> clazz, ByteBufSerializer<T> serializer, int id){
        serializerMap.put(clazz, serializer);
        classIdMap.put(clazz, id);
        idClassMap.put(id, clazz);
    }

    public void serialize(Object object, ByteBuffer buffer){
        if(object==null) {
            buffer.writeBoolean(false);
            return;
        }
        buffer.writeBoolean(true);
        ByteBufSerializer serializer = serializerMap.get(object.getClass());
        if(serializer == null) throw new IllegalArgumentException("No serializer found for class " + object.getClass());
        buffer.writeInt(classIdMap.get(object.getClass()));
        serializer.serialize(object, buffer);
    }

    public Object deserialize(ByteBuffer buffer) throws MissingClassException, MissingSerializerException {
        if(!buffer.readBoolean()) return null;
        int id = buffer.readInt();

        Class clazz = idClassMap.get(id);
        if(clazz==null) throw new MissingClassException("No class found for id " + id);

        ByteBufSerializer serializer = serializerMap.get(clazz);
        if(serializer == null) throw new MissingSerializerException("No serializer found for class " + clazz);

        return serializer.deserialize(buffer);
    }

    public Properties createNewProps(){
        return new Properties(this);
    }

}
