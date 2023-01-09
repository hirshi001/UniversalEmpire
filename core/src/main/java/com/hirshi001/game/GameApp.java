package com.hirshi001.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.hirshi001.buffer.bufferfactory.BufferFactory;
import com.hirshi001.game.screens.ErrorScreen;
import com.hirshi001.game.screens.FirstScreen;
import com.hirshi001.game.shared.packets.PingPacket;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.settings.Network;
import com.hirshi001.networking.network.NetworkFactory;
import com.hirshi001.networking.network.client.Client;

import java.text.NumberFormat;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameApp extends Game {

	public BufferFactory bufferFactory;
	public NetworkFactory networkFactory;

	public Client client;
	public GameResources gameResources;
	public TextureAtlas atlas;
	public ClientField field;
	public Disposable disposeWhenClose;

	public static long PING = 0L;
	public final String ip;
	public final int port;

	public static GameApp Game(){
		return (GameApp) Gdx.app.getApplicationListener();
	}


	public GameApp(Disposable disposeWhenClose, BufferFactory bufferFactory, NetworkFactory networkFactory, String ip, int port) {
		super();
		this.disposeWhenClose = disposeWhenClose;
		this.ip = ip;
		this.port = port;
		this.bufferFactory = bufferFactory;
		this.networkFactory = networkFactory;
		GameSettings.BUFFER_FACTORY = bufferFactory;
		GameSettings.registerSerializers();
	}



	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		atlas = new TextureAtlas("resources/spriteSheets/WizardSpriteSheet.atlas");
		GameSettings.runnablePoster = Gdx.app::postRunnable;
		Util.loadUtil();
		setScreen(new FirstScreen(this));
	}

	float time = 0F;
	float t2 = 0F;
	@Override
	public void render() {

		ScreenUtils.clear(0, 0, 0, 1F);

		/*
		time += Gdx.graphics.getDeltaTime();
		if(client!=null && client.isOpen() && time>=1F) {
			client.getChannel().sendTCPWithResponse(new PingPacket(System.currentTimeMillis()), null, 1000).onFailure((a) -> {
				GameApp.PING = 1000;
				System.out.println("Failure");
			}).then((ctx) -> {
				GameApp.PING = System.currentTimeMillis() - ((PingPacket) ctx.packet).time;
				System.out.println("PING " + GameApp.PING);
			}).perform();
			time = 0F;
		}

		 */

		try {
			super.render();
		}catch (Throwable t){
			Gdx.app.error("Error", "Error in screen: " + getScreen(), t);
			Gdx.app.log("Error", "Error in screen: " + getScreen());
			Gdx.app.log("Error", "Error in screen: " + getScreen(), t);
			setScreen(new ErrorScreen(this, t));
		}

		/*
		t2 += Gdx.graphics.getDeltaTime();
		if(t2>=1F){
			int bytes = Network.PACKET_ENCODER_DECODER.decodedBytes.getAndSet(0);
			// System.out.println("Bytes = " + bytes);
			System.out.println("Bytes per second = " + bytes / t2);
			System.out.println("Max packet size: " + Network.PACKET_ENCODER_DECODER.maxPacketSize);
			t2 = 0F;
		}

		 */
		System.gc();
	}

	@Override
	public void dispose() {
		super.dispose();
		if(client!=null) client.close();
		if(disposeWhenClose!=null) disposeWhenClose.dispose();
	}
}
