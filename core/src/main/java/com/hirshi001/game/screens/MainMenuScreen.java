package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.widgets.GameButton;
import com.hirshi001.game.widgets.GameTextField;

public class MainMenuScreen extends GameScreen {

    Stage stage;
    GameTextField ipField;
    GameButton startButton;
    GameButton exitButton;

    public MainMenuScreen(GameApp gameApp) {
        super(gameApp);
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(1000, 1000));
        stage.setDebugAll(true);


        Table table = new Table();
        table.setFillParent(true);

        ipField = new GameTextField(750, 200, 25, "");
        ipField.setMessageText("Enter IP Address");
        ipField.setAlignment(Align.center);
        table.add(ipField).width(750F).height(100F);


        startButton = new GameButton(750, 200, 25, "start");
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ConnectingScreen connectingScreen =  new ConnectingScreen(app);
                // connectingScreen.connect(ipField.getText());
                app.setScreen(connectingScreen);
            }
        });

        table.row();
        table.add(startButton).width(750F).height(100F).padTop(100F);

        exitButton = new GameButton(750, 200, 25, "exit");
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.row();
        table.add(exitButton).width(750F).height(100F).padTop(100F);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GREEN);

        //stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

