package com.hirshi001.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import com.hirshi001.buffer.bufferfactory.BufferFactory;
import com.hirshi001.game.screens.ErrorScreen;
import com.hirshi001.game.screens.FirstScreen;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.settings.Network;
import com.hirshi001.networking.network.NetworkFactory;
import com.hirshi001.networking.network.client.Client;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameApp extends Game {

	public BufferFactory bufferFactory;
	public NetworkFactory networkFactory;

	public Client client;
	public GameResources gameResources;
	public TextureAtlas atlas;
	public ClientField field;

	public static GameApp Game(){
		return (GameApp) Gdx.app.getApplicationListener();
	}


	public GameApp(BufferFactory bufferFactory, NetworkFactory networkFactory) {
		super();
		this.bufferFactory = bufferFactory;
		this.networkFactory = networkFactory;
		GameSettings.BUFFER_FACTORY = bufferFactory;
		GameSettings.registerSerializers();


	}



	@Override
	public void create() {
		atlas = new TextureAtlas("resources/spriteSheets/WizardSpriteSheet.atlas");
		GameSettings.runnablePoster = Gdx.app::postRunnable;
		Util.loadUtil();
		// Gdx.graphics.setUndecorated(true);
		setScreen(new FirstScreen(this));
	}

	@Override
	public void render() {

		ScreenUtils.clear(0, 0, 0, 1F);


		try {
			super.render();
		}catch (Throwable t){
			t.printStackTrace();
			setScreen(new ErrorScreen(this, t));
		}
		int bytes = Network.PACKET_ENCODER_DECODER.decodedBytes.getAndSet(0);
		// System.out.println("Bytes = " + bytes);
		// System.out.println("Bytes per second = " + bytes / Gdx.graphics.getDeltaTime());


	}
}
