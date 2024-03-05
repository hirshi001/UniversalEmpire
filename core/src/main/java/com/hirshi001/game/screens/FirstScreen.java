package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.*;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.GameResources;
import com.hirshi001.game.util.FreetypeFontGeneratorSerializer;

import java.io.IOException;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class FirstScreen extends GameScreen {

    SpriteBatch batch;

    Sprite titleSprite;
    boolean isWaiting = false;

    AssetManager loader;

    public FirstScreen(GameApp gameApp) {
        super(gameApp);

    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        if (GameApp.gameResources == null) {
            JsonReader jsonReader = new JsonReader();
            JsonValue base = jsonReader.parse(Gdx.files.internal("resources/GameElements/ResourceMap.json"));
            GameApp.gameResources = new GameResources(base);
        }
        if (GameApp.guiSkin == null) {

            GameApp.guiSkin = new Skin(Gdx.files.internal("skins/GUISkin.json")) {
                //Override json loader to process FreeType fonts from skin JSON
                @Override
                protected Json getJsonLoader(final FileHandle skinFile) {
                    Json json = super.getJsonLoader(skinFile);
                    final Skin skin = this;
                    json.setSerializer(FreeTypeFontGenerator.class, new FreetypeFontGeneratorSerializer(skin, skinFile));
                    return json;
                }
            };

        }

        titleSprite = new Sprite(new Texture("resources/StartScreen/UniversalEmpireTitle.png"));
        titleSprite.setOriginBasedPosition(Gdx.graphics.getWidth() / 2F, Gdx.graphics.getHeight() / 2F);
        titleSprite.setAlpha(0F);
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        // batch.enableBlending();
        ScreenUtils.clear(Color.WHITE);

        if (!isWaiting) {
            titleSprite.setAlpha(titleSprite.getColor().a + delta / 1F);
            if (titleSprite.getColor().a >= 1F) {
                isWaiting = true;
            }
        }
        try {
            app.gameResources.update();
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }

        if (isWaiting && app.gameResources.isFinished()) {
            //app.setScreen(new MainMenuScreen(app));
            ConnectingScreen connectingScreen = new ConnectingScreen(app);
            Gdx.app.log("First Screen", "About to connect to server:" + app.ip + ":" + app.port);
            connectingScreen.connect(GameApp.Game().ip, GameApp.Game().port); // 54.219.108.146
            app.setScreen(connectingScreen);
        } else {
            batch.begin();
            titleSprite.draw(batch);
            batch.end();
        }

    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        batch.dispose();
    }
}
