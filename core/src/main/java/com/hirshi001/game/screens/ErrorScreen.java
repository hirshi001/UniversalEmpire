package com.hirshi001.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.widgets.GameTextFieldStyle;

import static com.hirshi001.game.Util.*;

public class ErrorScreen extends GameScreen {

    private Throwable cause;

    Stage stage;
    Table table;

    public ErrorScreen(GameApp gameApp, Throwable cause) {
        super(gameApp);
        this.cause = cause;

    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(1000, 1000));
        table = new Table();
        table.setFillParent(true);


        TextureRegion border = new TextureRegion((Texture) GameApp.Game().gameResources.get(BORDER_TEXTURE));
        TextureRegion background = new TextureRegion((Texture) GameApp.Game().gameResources.get(BACKGROUND_TEXTURE));
        BitmapFont font = GameApp.Game().gameResources.get(FONT);
        TextArea textArea = new TextArea(cause.toString(), new GameTextFieldStyle(500, 500, 5, border, background, font));
        textArea.setAlignment(Align.center);
        table.add(textArea).expand().fill();

        stage.addActor(table);

        cause.printStackTrace();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GREEN);

        stage.act(delta);
        stage.draw();

    }
}
