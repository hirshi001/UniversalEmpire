package com.hirshi001.game.shared.registry;

public abstract class ObjectHolderSupplier<T extends ID> implements ID {

    private int id;

    public final T get(){
        T obj = create();
        obj.setID(id);
        return obj;
    }

    protected abstract T create();


    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }
}
