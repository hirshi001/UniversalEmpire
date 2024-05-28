package com.hirshi001.game.shared.util.props;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.game.shared.registry.DefaultRegistry;
import com.hirshi001.game.shared.registry.ObjectHolder;
import com.hirshi001.game.shared.registry.Registry;
import com.hirshi001.game.shared.util.stringutils.StringUtils;
import com.hirshi001.networking.packet.ByteBufSerializable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class Properties implements ByteBufSerializable, Iterable<String> {

    protected Map<String, Integer> name_id_map = new HashMap<>();
    protected Registry<ObjectHolder<String>> id_name_map = new DefaultRegistry<>();

    protected Map<String, Object> properties = new HashMap<>(), localProperties = new HashMap<>();

    protected List<String> modifiedProperties = new CopyOnWriteArrayList<>(), localModifiedProperties = new CopyOnWriteArrayList<>();

    protected PropertiesManager manager;

    protected int id = 0;

    public Properties(PropertiesManager manager) {
        this.manager = manager;
    }


    public void put(String key, Object object) {
        checkKey(key);
        properties.put(key, object);
        modifiedProperties.add(key);
    }

    public void putLocal(String key, Object object) {
        localProperties.put(key, object);
        localModifiedProperties.add(key);
    }

    public void put(String key, int id, Object value) {
        name_id_map.put(key, id);
        id_name_map.register(new ObjectHolder<>(key), id);
        properties.put(key, value);
        modifiedProperties.add(key);
    }

    public <T> void update(String key, Function<T, ?> function) {
        put(key, function.apply((T) properties.get(key)));
    }

    public int size() {
        return properties.size();
    }

    public List<String> getModifiedProperties() {
        return modifiedProperties;
    }

    public List<String> getLocalModifiedProperties() {
        return localModifiedProperties;
    }

    private void checkKey(String key) {
        if (!name_id_map.containsKey(key)) {
            name_id_map.put(key, id);
            id_name_map.register(new ObjectHolder<>(key), id);
            id++;
        }
    }

    public <T> T get(String key) {
        T value = (T) properties.get(key);
        if (value == null) {
            value = (T) localProperties.get(key);
        }
        return value;
    }

    public <T> T get(String key, T defaultValue) {
        T value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public Integer getId(String key) {
        return name_id_map.get(key);
    }

    public String getKeyName(int id) {
        ObjectHolder<String> holder = id_name_map.get(id);
        if (holder == null) return null;
        return holder.get();
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        buffer.writeInt(properties.size());
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            buffer.writeInt(name_id_map.get(entry.getKey()));
            ByteBufUtil.writeStringToBuf(entry.getKey(), buffer);
            manager.serialize(entry.getValue(), buffer);
        }
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            int id = buffer.readInt();
            String key = ByteBufUtil.readStringFromBuf(buffer);
            try {
                Object value = manager.deserialize(buffer);
                put(key, id, value);
            } catch (MissingClassException | MissingSerializerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return properties.keySet().iterator();
    }

    @Override
    public Spliterator<String> spliterator() {
        return properties.keySet().spliterator();
    }

    @Override
    public String toString() {
        if (size() == 0) return "no properties";
        StringBuilder builder = new StringBuilder();
        for (String property : this) {
            builder.append(property).append("=").append(StringUtils.DEFAULT().toString(get(property)));
            Integer id = getId(property);
            if(id!=null) builder.append(" : ").append(id);
            builder.append('\n');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
