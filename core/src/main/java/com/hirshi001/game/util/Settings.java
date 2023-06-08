package com.hirshi001.game.util;

import com.hirshi001.game.GameApp;

public class Settings {

    public static Setting<Float> zoom = new Setting<>(1F, (oldVal, newVal)-> {
        GameApp.fieldRenderer.camera.zoom = newVal;
        return true;
    });

    public static Setting<Integer> chunkLoadRadius = new Setting<>(2, (oldVal, newVal)-> {
        GameApp.field.chunkLoadRadius = newVal;
        return true;
    });

    private Settings(){

    }

}

