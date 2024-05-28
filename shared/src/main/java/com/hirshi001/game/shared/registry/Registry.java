package com.hirshi001.game.shared.registry;

import java.util.Collection;

public interface Registry<T extends ID> {

    void register(T obj, int id);

    T get(int id);

    T remove(int id);

    void get(Collection<T> collection);

}
