package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
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
import com.hirshi001.game.GameResources;
import com.hirshi001.game.widgets.GameTextFieldStyle;

import java.util.Arrays;

import static com.hirshi001.game.util.Util.*;

public class ErrorScreen extends GameScreen {

    private Throwable cause;

    Stage stage;
    Table table;

    public static final String BORDER_TEXTURE = "button_border";
    public static final String BACKGROUND_TEXTURE = "button_background";
    public static final String FONT = "font-256.fnt";


    public ErrorScreen(GameApp gameApp, Throwable cause) {
        super(gameApp);
        this.cause = cause;
        cause.printStackTrace();

    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(1000, 1000));
        table = new Table();
        table.setFillParent(true);


        GameResources resources = app.gameResources;

        TextureRegion border = resources.getFromAtlas(BORDER_TEXTURE);
        TextureRegion background = resources.getFromAtlas(BACKGROUND_TEXTURE);
        BitmapFont font = resources.get(FONT);
        TextArea textArea = new TextArea(cause.toString()+"\n"+ Arrays.toString(cause.getStackTrace()), new GameTextFieldStyle(500, 500, 5, border, background, font));
        textArea.setAlignment(Align.center);
        table.add(textArea).expand().fill();

        stage.addActor(table);
        Gdx.app.log("Error Screen", "Error Screen Created", cause);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GREEN);

        stage.act(delta);
        stage.draw();

    }
}
