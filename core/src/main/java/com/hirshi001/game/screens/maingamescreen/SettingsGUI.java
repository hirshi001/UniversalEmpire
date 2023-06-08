package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.util.Settings;

public class SettingsGUI extends ScrollPane {

    public SettingsGUI() {
        super(null, GameApp.guiSkin.get("OpaquePane", ScrollPaneStyle.class));

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.defaults().pad(10F);

        // ZOOM
        table.add(new Label("Zoom", GameApp.guiSkin)).uniformX().fillX();

        Slider zoomSlider = new Slider(0.1F, 100F, 0.1F, false, GameApp.guiSkin);
        Settings.zoom.value = GameApp.fieldRenderer.camera.zoom;
        zoomSlider.setValue(Settings.zoom.value);
        zoomSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.zoom.change(zoomSlider.getValue());
            }
        });
        table.add(zoomSlider).uniformX().fillX();
        table.row();

        // CHUNK LOAD RADIUS
        table.add(new Label("Chunk Load Radius", GameApp.guiSkin)).uniformX().fillX();

        Slider chunkLoadRadiusSlider = new Slider(1, 5, 1, false, GameApp.guiSkin);
        Settings.chunkLoadRadius.value = GameApp.field.chunkLoadRadius;
        chunkLoadRadiusSlider.setValue(Settings.chunkLoadRadius.value);
        chunkLoadRadiusSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.chunkLoadRadius.change((int) chunkLoadRadiusSlider.getValue());
            }
        });
        table.add(chunkLoadRadiusSlider).uniformX().fillX();
        table.row();


        TextButton backButton = new TextButton("Back", GameApp.guiSkin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Settings.zoom.apply(); // Special case since scroll zoom is allowed while in Settings GUI
                Settings.chunkLoadRadius.apply();
                GameApp.removeGameGui(SettingsGUI.this);
            }
        });
        table.add(backButton).colspan(2).growX().row();

        setActor(table);

    }


}
