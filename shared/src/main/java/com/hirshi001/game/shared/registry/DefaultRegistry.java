package com.hirshi001.game.shared.registry;

public class DefaultRegistry<T extends ID> implements Registry<T> {

    private Object[] items;
    private final Object lock = new Object();

    public DefaultRegistry() {
        items = new Object[16];
    }

    @Override
    public void register(T obj, int id) {
        synchronized (lock) {
            if (id >= items.length) {
                resize(id);
            }
            items[id] = obj;
            obj.setID(id);
        }
    }

    private void resize(int size){
        synchronized (lock){
            int newSize = items.length*2;
            if(newSize < size){
                newSize = size;
            }
            Object[] newItems = new Object[newSize];
            System.arraycopy(items, 0, newItems, 0, items.length);
            items = newItems;
        }
    }

    @Override
    public T get(int id) {
        return (T)items[id];
    }

}
