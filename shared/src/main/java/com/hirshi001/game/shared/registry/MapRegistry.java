package com.hirshi001.game.shared.registry;

import com.badlogic.gdx.utils.IntMap;

import java.util.Collection;

public class MapRegistry<T extends ID> implements Registry<T> {

    private final IntMap<T> map = new IntMap<T>();


    @Override
    public void register(T obj, int id) {
        map.put(id, obj);
        obj.setID(id);
    }

    @Override
    public T get(int id) {
        return map.get(id);
    }

    @Override
    public T remove(int id) {
        return map.remove(id);
    }

    @Override
    public void get(Collection<T> collection) {
        map.values().forEach(collection::add);
    }

}
