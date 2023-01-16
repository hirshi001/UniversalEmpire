package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.GameResources;

import java.io.IOException;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen extends GameScreen {

	SpriteBatch batch;

	Sprite titleSprite;
	boolean isWaiting = false;

	public FirstScreen(GameApp gameApp) {
		super(gameApp);

	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		if(app.gameResources == null){
			JsonReader jsonReader = new JsonReader();
			JsonValue base = jsonReader.parse(Gdx.files.internal("resources/GameElements/ResourceMap.json"));
			app.gameResources = new GameResources(base);
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

		if(!isWaiting){
			titleSprite.setAlpha(titleSprite.getColor().a + delta/1F);
			if(titleSprite.getColor().a>=1F){
				isWaiting = true;
			}
		}
		try {
			app.gameResources.update();
		} catch (GdxRuntimeException e) {
			e.printStackTrace();
		}

		if(isWaiting && app.gameResources.isFinished()){
			//app.setScreen(new MainMenuScreen(app));
			ConnectingScreen connectingScreen = new ConnectingScreen(app);
			Gdx.app.log("About to connect to server:", app.ip+":"+app.port);
			connectingScreen.connect(GameApp.Game().ip, GameApp.Game().port); // 54.219.108.146
			app.setScreen(connectingScreen);
		}
		else {
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
