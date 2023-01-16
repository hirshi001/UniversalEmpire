package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.hirshi001.game.GameApp;

public class MainMenuScreen extends GameScreen {

    TextButton createAccountButton, loginButton;
    Texture ninePatchTexture;
    Texture bitmapFontTexture;

    Stage stage;

    public MainMenuScreen(GameApp gameApp) {
        super(gameApp);
        this.ninePatchTexture = new Texture("resources/MenuScreens/Buttons.png");
        bitmapFontTexture = new Texture("resources/MenuScreens/opensans-16.png");
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    @Override
    public void show() {
        super.show();
        NinePatch ninePatch = new NinePatch(ninePatchTexture, 10, 10, 10, 10);
        NinePatchDrawable drawable = new NinePatchDrawable(ninePatch);
        BitmapFont font = new BitmapFont(Gdx.files.internal("resources/MenuScreens/opensans-16.fnt"), new TextureRegion(bitmapFontTexture));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(drawable, drawable, drawable, font);

        Table table = new Table();

        Value width = Value.percentWidth(0.8F, table);
        createAccountButton = new TextButton("Create Account", style);
        createAccountButton.setTransform(true);
        table.add(createAccountButton).minWidth(width).fill();

        table.row().padTop(20);

        loginButton = new TextButton("Login to Existing Account", style);
        loginButton.setTransform(true);
        table.add(loginButton).minWidth(width).fill();

        table.setFillParent(true);
        table.setTransform(true);
        stage.addActor(table);
        stage.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);



        createAccountButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(new CreateAccountScreen(app));
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        bitmapFontTexture.dispose();
        ninePatchTexture.dispose();

    }
}
