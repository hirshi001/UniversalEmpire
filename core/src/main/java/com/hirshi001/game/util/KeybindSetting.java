package com.hirshi001.game.util;

public class KeybindSetting extends Setting<Integer> {

    public KeybindSetting(String name, Integer initialValue, SettingListener<Integer> changeListener) {
        super(name, initialValue, changeListener);
    }


    public KeybindSetting(String name, Integer initialValue) {
        super(name, initialValue, (oldValue, newValue) -> true);
    }


}
