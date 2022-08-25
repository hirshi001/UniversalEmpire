package com.hirshi001.game.shared.util;

import java.util.ArrayDeque;
import java.util.function.Supplier;

public class Resources<T> {

    private final ArrayDeque<T> storage;
    private final Supplier<T> supplier;

    public Resources(int initialSize, Supplier<T> supplier){
        this.storage = new ArrayDeque<>();
        this.supplier = supplier;
        for(int i=0;i<initialSize;i++){
            storage.push(supplier.get());
        }
    }

    public T get(){
        if(storage.size()==0) return supplier.get();
        return storage.pop();
    }

    public void release(T object){
        storage.push(object);
    }

}
