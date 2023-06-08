package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.tommyettinger.textra.TextraLabel;
import com.github.tommyettinger.textra.TypingLabel;
import com.github.tommyettinger.textra.TypingTooltip;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.widgets.Styles;

public class InputGui extends Table {

    InputListener listener;

    TextraLabel label;
    TextField textField;

    TextButton doneButton;
    Window inputSelectionGui = new Window("", GameApp.guiSkin);


    public InputGui(String text, InputListener listener) {
        super(GameApp.guiSkin);
        this.listener = listener;

        label = new TextraLabel(text, GameApp.guiSkin);


        textField = new TextField("", GameApp.guiSkin);
        doneButton = new TextButton("Done", GameApp.guiSkin);

        inputSelectionGui.defaults().pad(10F);
        inputSelectionGui.add(label).uniformX().fillX();
        inputSelectionGui.add(textField).uniformX().fillX();
        inputSelectionGui.row();
        inputSelectionGui.add(doneButton).colspan(2).growX().row();

        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                listener.onInput(textField.getText(), InputGui.this);
            }
        });

        add(inputSelectionGui).center();
        center();

    }


    interface InputListener {
        void onInput(String input, InputGui gui);

        default void onCancel() {
        }
    }
}
