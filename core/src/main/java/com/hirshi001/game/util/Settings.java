package com.hirshi001.game.util;

import com.badlogic.gdx.Input;
import com.hirshi001.game.GameApp;
import com.hirshi001.networking.util.defaultpackets.systempackets.NetworkConditionPackets;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    public static FloatSliderSetting zoom = new FloatSliderSetting("zoom", 0F, (oldVal, newVal)-> {
        GameApp.fieldRenderer.camera.zoom = (float)Math.pow(2.0f, newVal); // play with 2.0f
        GameApp.fieldRenderer.camera.update();
        return true;
    }, -20F, 20F, 0.1F);

    public static IntegerSliderSetting chunkLoadRadius = new IntegerSliderSetting("chunk load radius", 2, (oldVal, newVal)-> {
        GameApp.field.chunkLoadRadius = newVal;
        return true;
    }, 1, 10, 1);


    public static FloatSliderSetting latency = new FloatSliderSetting("latency", 0F, (oldVal, newVal)-> {
        GameApp.client.getChannel().sendTCP(new NetworkConditionPackets.LatencyPacket(newVal), null).performAsync();
        System.out.println("New latency: " + newVal);
        return true;
    }, 0F, 2F, 0.01F);

    public static FloatSliderSetting packetLossRate = new FloatSliderSetting("packet loss rate", 0F, (oldVal, newVal)-> {
        GameApp.client.getChannel().sendTCP(new NetworkConditionPackets.PacketLossPacket(newVal), null).performAsync();
        return true;
    }, 0F, 1F, 0.01F);

    public static BooleanSetting enableNetworkCondition = new BooleanSetting("enable network condition", false, (oldVal, newVal)-> {
        GameApp.client.getChannel().sendTCP(new NetworkConditionPackets.EnableNetworkConditionPacket(newVal), null).performAsync();
        System.out.println("Enabling network condition: " + newVal);
        return true;
    });


    public static KeybindSetting moveUp = new KeybindSetting("move up", Input.Keys.W);
    public static KeybindSetting moveDown = new KeybindSetting("move down", Input.Keys.S);
    public static KeybindSetting moveLeft = new KeybindSetting("move left", Input.Keys.A);
    public static KeybindSetting moveRight = new KeybindSetting("move right", Input.Keys.D);

    public static KeybindSetting buildEditMode = new KeybindSetting("build/edit mode", Input.Keys.B);
    public static KeybindSetting troopFormationMode = new KeybindSetting("troop formation mode", Input.Keys.F);

    public static List<Setting<?>> settings = new ArrayList<>();

    static {
        settings.add(zoom);
        settings.add(chunkLoadRadius);
        settings.add(latency);
        settings.add(packetLossRate);
        settings.add(enableNetworkCondition);
        settings.add(moveUp);
        settings.add(moveDown);
        settings.add(moveLeft);
        settings.add(moveRight);
        settings.add(buildEditMode);
        settings.add(troopFormationMode);
    }

    private Settings(){

    }

}

