package com.hirshi001.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.hirshi001.buffer.bufferfactory.BufferFactory;
import com.hirshi001.game.render.FieldRender;
import com.hirshi001.game.screens.ErrorScreen;
import com.hirshi001.game.screens.FirstScreen;
import com.hirshi001.game.screens.GameScreen;
import com.hirshi001.game.screens.MainMenuScreen;
import com.hirshi001.game.screens.maingamescreen.GameGUI;
import com.hirshi001.game.screens.maingamescreen.MainGameScreen;
import com.hirshi001.game.shared.packets.PingPacket;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.settings.Network;
import com.hirshi001.networking.network.NetworkFactory;
import com.hirshi001.networking.network.client.Client;
import com.hirshi001.networking.packetdecoderencoder.SimplePacketEncoderDecoder;

import java.text.NumberFormat;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameApp extends Game {

	public static BufferFactory bufferFactory;
	public static NetworkFactory networkFactory;

	public static Client client;
	public static GameResources gameResources;
	public static Skin guiSkin;
	public static ClientField field;
	public static FieldRender fieldRenderer;
	public Disposable disposeWhenClose;


	public static long PING = 0L;
	public final String ip;
	public final int port;

	public static GameApp Game(){
		return (GameApp) Gdx.app.getApplicationListener();
	}

	public static void addGameGui(Actor gameGUI){
		Screen screen = Game().getScreen();
		if(screen instanceof MainGameScreen){
			((MainGameScreen) screen).addGameGUI(gameGUI);
		}
	}

	public static boolean removeGameGui(Actor gameGUI){
		Screen screen = Game().getScreen();
		if(screen instanceof MainGameScreen){
			return ((MainGameScreen) screen).removeGameGUI(gameGUI);
		}
		return false;
	}

	public static Stage guiStage(){
		Screen screen = Game().getScreen();
		if(screen instanceof MainGameScreen){
			return ((MainGameScreen) screen).getGuiStage();
		}
		return null;
	}


	public GameApp(Disposable disposeWhenClose, BufferFactory bufferFactory, NetworkFactory networkFactory, String ip, int port) {
		super();
		this.disposeWhenClose = disposeWhenClose;
		this.ip = ip;
		this.port = port;
		GameApp.bufferFactory = bufferFactory;
		GameApp.networkFactory = networkFactory;
		GameSettings.BUFFER_FACTORY = bufferFactory;
		GameSettings.registerSerializers();
	}



	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		GameSettings.runnablePoster = Gdx.app::postRunnable;
		// setScreen(new FirstScreen(this));
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

		if(client!=null && client.isOpen()) {
			if(client.tcpOpen()) client.checkTCPPackets();
			if(client.udpOpen()) client.checkUDPPackets();
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
