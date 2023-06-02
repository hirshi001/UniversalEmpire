package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.hirshi001.game.widgets.Styles;

public class InputGui extends Table {

    InputListener listener;

    TextField label;
    TextField textField;

    TextButton doneButton;
    GameGUI inputSelectionGui = new GameGUI();


    public InputGui(String text, InputListener listener) {
        super();
        this.listener = listener;

        label = new TextField(text, Styles.textFieldStyle);
        label.setDisabled(true);

        textField = new TextField("", Styles.textFieldStyle);

        doneButton = new TextButton("Done", Styles.textButtonStyle);

        inputSelectionGui.add(label).expand();
        inputSelectionGui.add(textField).expand();
        inputSelectionGui.row().padTop(10F);
        inputSelectionGui.add(doneButton).colspan(2).expand().row();

        doneButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                listener.onInput(textField.getText(), InputGui.this);
            }
        });

        add(inputSelectionGui).center();
        center();

    }


    interface InputListener{
        void onInput(String input, InputGui gui);
        default void onCancel(){}
    }
}
