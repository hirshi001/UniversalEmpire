package com.hirshi001.game.util;

public class FloatSliderSetting extends Setting<Float>{

    public float min, max, step;

    public FloatSliderSetting(String name, Float initialValue, SettingListener<Float> changeListener, float min, float max, float step){
        super(name, initialValue, changeListener);
        this.min = min;
        this.max = max;
        this.step = step;

    }

}
