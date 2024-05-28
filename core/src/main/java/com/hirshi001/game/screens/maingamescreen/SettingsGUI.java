package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.util.*;

public class SettingsGUI extends ScrollPane {

    public SettingsGUI() {
        super(null, GameApp.guiSkin.get("OpaquePane", ScrollPaneStyle.class));
        setScrollbarsVisible(true);
        // setScrollbarsOnTop(true);
        setFadeScrollBars(false);
        setScrollingDisabled(true, false);
        setFillParent(true);

        Table table = new Table();
        // table.padRight(50);
       //  table.padLeft(50);
        table.padTop(20);
        table.padBottom(20);
        table.center();

        table.defaults().pad(10F);
        table.columnDefaults(0).expandX();
        table.columnDefaults(1).expandX();

        for(Setting<?> setting:Settings.settings){
            Label label = new Label(setting.name, GameApp.guiSkin);
            label.setAlignment(Align.center);
            table.add(label).uniformX().fillX().center();
            if(setting instanceof FloatSliderSetting) {
                addFloatSlider(table, (FloatSliderSetting) setting);
            }
            else if(setting instanceof IntegerSliderSetting){
                addIntegerSlider(table, (IntegerSliderSetting) setting);
            }
            else if(setting instanceof KeybindSetting) {
                addKeybind(table, (KeybindSetting) setting);
            }else if(setting instanceof BooleanSetting) {
                addBoolean(table, (BooleanSetting) setting);
            }
            table.row();
        }



        TextButton backButton = new TextButton("Back", GameApp.guiSkin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for(Setting<?> setting:Settings.settings){
                    setting.apply();
                }
                GameApp.removeGameGui(SettingsGUI.this);
            }
        });
        table.add(backButton).colspan(2).growX().row();


        setActor(table);
        layout();
    }

    public void addFloatSlider(Table table, FloatSliderSetting setting){
        Slider slider = new Slider(setting.min, setting.max, setting.step, false, GameApp.guiSkin);
        slider.setValue(Settings.zoom.value);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setting.change(slider.getValue());
            }
        });
        table.add(slider).uniformX().fillX();
    }

    public void addIntegerSlider(Table table, IntegerSliderSetting setting){
        Slider slider = new Slider(setting.min, setting.max, setting.step, false, GameApp.guiSkin);
        slider.setValue(Settings.zoom.value);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setting.change((int)slider.getValue());
            }
        });
        table.add(slider).uniformX().fillX();
    }

    public void addKeybind(Table table, KeybindSetting setting){
        TextButton button = new TextButton(Input.Keys.toString(setting.value), GameApp.guiSkin);
        button.addListener(new ClickListener() {

            InputProcessor old;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                button.getStage().setKeyboardFocus(button);
                old = Gdx.input.getInputProcessor();
                Gdx.input.setInputProcessor(new InputAdapter(){
                    @Override
                    public boolean keyDown(int keycode) {
                        if (keycode != Input.Keys.ESCAPE) {
                            setting.change(keycode);
                            button.setText(Input.Keys.toString(keycode));
                        }
                        Gdx.input.setInputProcessor(old);
                        return true;
                    }
                });
            }
        });

        table.add(button).uniformX().growX().maxWidth(Value.percentWidth(0.25F, table));

    }

    public void addBoolean(Table table, BooleanSetting setting){
        CheckBox checkBox = new CheckBox("", GameApp.guiSkin);
        checkBox.setChecked(setting.value);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setting.change(checkBox.isChecked());
            }
        });
        table.add(checkBox).uniformX().fillX();
    }


}
