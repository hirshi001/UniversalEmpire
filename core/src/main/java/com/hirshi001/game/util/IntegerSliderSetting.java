package com.hirshi001.game.util;

public class IntegerSliderSetting extends Setting<Integer>{

    public float min, max, step;

    public IntegerSliderSetting(String name, Integer initialValue, SettingListener<Integer> changeListener, int min, int max, int step){
        super(name, initialValue, changeListener);
        this.min = min;
        this.max = max;
        this.step = step;
    }

}
