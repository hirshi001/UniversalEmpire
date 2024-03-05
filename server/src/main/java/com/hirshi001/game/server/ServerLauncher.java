package com.hirshi001.game.server;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.buffer.bufferfactory.BufferFactory;
import com.hirshi001.buffer.bufferfactory.DefaultBufferFactory;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.GamePieces;
import com.hirshi001.game.shared.entities.TestGamePiece;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.settings.Network;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.javanetworking.JavaNetworkFactory;
import com.hirshi001.javarestapi.JavaRestFutureFactory;
import com.hirshi001.networking.network.NetworkFactory;
import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.network.channel.ChannelInitializer;
import com.hirshi001.networking.network.channel.ChannelOption;
import com.hirshi001.networking.network.server.AbstractServerListener;
import com.hirshi001.networking.network.server.Server;
import com.hirshi001.networking.network.server.ServerListener;
import com.hirshi001.networking.networkdata.DefaultNetworkData;
import com.hirshi001.networking.networkdata.NetworkData;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;
import com.hirshi001.networking.packetregistrycontainer.SinglePacketRegistryContainer;
import com.hirshi001.restapi.RestAPI;
import com.hirshi001.websocketnetworkingserver.WebsocketServer;
import logger.ConsoleColors;
import logger.DateStringFunction;
import logger.Logger;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Launches the server application.
 */
public class ServerLauncher {



    public static void main(String[] args) throws Exception {

        int websocketPort, javaPort;
        String KEYSTORE_PASSWORD = null;

        if (args.length == 3) {
            websocketPort = Integer.parseInt(args[0]);
            javaPort = Integer.parseInt(args[1]);
            KEYSTORE_PASSWORD = args[2];
        } else if (args.length == 2) {
            websocketPort = Integer.parseInt(args[0]);
            javaPort = Integer.parseInt(args[1]);
        } else if (args.length == 1) {
            javaPort = Network.JAVA_PORT;
            websocketPort = Network.WEBSOCKET_PORT;
            KEYSTORE_PASSWORD = args[0];
        } else {
            javaPort = Network.JAVA_PORT;
            websocketPort = Network.WEBSOCKET_PORT;
        }

        createApplication(websocketPort, javaPort, KEYSTORE_PASSWORD);
    }


    private static Application createApplication(int websocketPort, int javaPort, String KEYSTORE_PASSWORD) {
        // Note: you can use a custom ApplicationListener implementation for the headless project instead of GameApp.
        return new HeadlessApplication(new ServerApplication(websocketPort, javaPort, KEYSTORE_PASSWORD), getDefaultConfiguration());
    }

    private static HeadlessApplicationConfiguration getDefaultConfiguration() {
        HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
        configuration.updatesPerSecond = -1; // When this value is negative, GameApp#render() is never called.
        //// If the above line doesn't compile, it is probably because the project libGDX version is older.
        //// In that case, uncomment and use the below line.
        //configuration.renderInterval = -1f; // When this value is negative, GameApp#render() is never called.
        return configuration;
    }

}
