package com.hirshi001.game.util;

public class BooleanSetting extends Setting<Boolean> {

    public boolean value;

    public BooleanSetting(String name, Boolean initialValue, SettingListener<Boolean> changeListener) {
        super(name, initialValue, changeListener);
        this.value = initialValue;
    }
}
