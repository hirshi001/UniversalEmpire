package com.hirshi001.game.shared.registry;

public class ObjectHolder<T> implements ID{

    private T object;

    private int id;

    public ObjectHolder(T object){
        this.object = object;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    public T get(){
        return object;
    }
}
