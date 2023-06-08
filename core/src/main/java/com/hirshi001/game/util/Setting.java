package com.hirshi001.game.util;

public class Setting<T> {

    public T value;
    private T currentValue;
    public SettingListener<T> listener;

    public Setting(T initialValue, SettingListener<T> changeListener) {
        this.value = initialValue;
        this.currentValue = initialValue;
        this.listener = changeListener;
    }

    public void change(T newValue){
        if(listener != null){
            if(listener.onSettingChange(value, newValue)){
                currentValue = newValue;
            }
        }
        else currentValue = newValue;
    }

    public void apply(){
        if(listener != null){
            if(listener.onSettingApply(value, currentValue)){
                value = currentValue;
            }
        }
        else value = currentValue;
    }


    @FunctionalInterface
    interface SettingListener<T> {
        default boolean onSettingChange(T oldValue, T newValue){return onSettingApply(oldValue, newValue);}

        boolean onSettingApply(T oldValue, T newValue);
    }

}
