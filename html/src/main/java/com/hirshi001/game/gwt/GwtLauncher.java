package com.hirshi001.game.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.inject.OnCompletion;
import com.github.czyzby.websocket.GwtWebSockets;
import com.hirshi001.buffer.bufferfactory.DefaultBufferFactory;
import com.hirshi001.game.GameApp;
import com.hirshi001.gwtnetworking.GWTChannel;
import com.hirshi001.gwtnetworking.GWTNetworkingFactory;
import com.hirshi001.gwtnetworking.SecureGWTNetworkingFactory;
import com.hirshi001.gwtrestapi.GWTRestFutureFactory;
import com.hirshi001.restapi.RestAPI;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
		@Override
		public GwtApplicationConfiguration getConfig () {
			// Resizable application, uses available space in browser with no padding:
			GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
			cfg.padVertical = 0;
			cfg.padHorizontal = 0;
			return cfg;
			// If you want a fixed size application, comment out the above resizable section,
			// and uncomment below:
			//return new GwtApplicationConfiguration(640, 480);
		}

		@Override
		public ApplicationListener createApplicationListener () {
			GwtWebSockets.initiate();
			RestAPI.setFactory(new GWTRestFutureFactory());
			return new GameApp(null, new DefaultBufferFactory(), new GWTNetworkingFactory(), "localhost", 443);
		}

	@Override
	public void onModuleLoad() {
		FreetypeInjector.inject(GwtLauncher.super::onModuleLoad);
	}
}
