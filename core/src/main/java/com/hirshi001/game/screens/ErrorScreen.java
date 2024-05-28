package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.screens.maingamescreen.GameGUI;

import java.util.Arrays;

public class ErrorScreen extends GameScreen {

    private Throwable cause;

    Stage stage;
    GameGUI gui;




    public ErrorScreen(GameApp gameApp, Throwable cause) {
        super(gameApp);
        this.cause = cause;
        cause.printStackTrace();
        gui = new GameGUI("Error", app.guiSkin, Align.topRight, () -> app.setScreen(new FirstScreen(app)));
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(500, 500));
        gui.setFillParent(true);

        TextArea textArea = new TextArea(cause.toString()+"\n"+ Arrays.toString(cause.getStackTrace()), app.guiSkin);
        textArea.setAlignment(Align.center);
        gui.add(textArea).expand().fill();

        stage.addActor(gui);
        Gdx.app.log("Error Screen", "Error Screen Created", cause);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GREEN);

        stage.act(delta);
        stage.draw();

    }
}
