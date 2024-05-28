package com.hirshi001.game.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.hirshi001.buffer.bufferfactory.BufferFactory;
import com.hirshi001.buffer.bufferfactory.DefaultBufferFactory;
import com.hirshi001.game.shared.entities.GamePieces;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.settings.Network;
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
import com.hirshi001.networking.util.defaultpackets.systempackets.NetworkConditionPackets;
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
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerApplication extends ApplicationAdapter {


    public static NetworkFactory networkFactory;
    public static BufferFactory bufferFactory;
    public static ServerField field;
    public static ScheduledExecutorService executorService;

    public static Logger logger;
    public static PrintStream sysout, syserr;

    private int websocketPort, javaPort;
    private String keystorePassword;
    private Server javaServer, websocketServer;
    CommandHandler commandHandler;

    public ServerApplication(int websocketPort, int javaPort, String keystorePassword) {
        super();
        this.websocketPort = websocketPort;
        this.javaPort = javaPort;
        this.keystorePassword = keystorePassword;
    }

    @Override
    public void create() {

        sysout = System.out;
        syserr = System.err;
        logger = new Logger(System.out, System.err,
                new DateStringFunction(ConsoleColors.CYAN, "[", "]")
        );
        logger.debug();
        logger.debugShort(true);
        System.setOut(logger);
        System.out.println("Starting server...");


        RestAPI.setFactory(new JavaRestFutureFactory());
        executorService = Executors.newScheduledThreadPool(3);
        networkFactory = new JavaNetworkFactory(executorService);
        bufferFactory = new DefaultBufferFactory();
        GameSettings.BUFFER_FACTORY = bufferFactory;
        try {
            startServer(websocketPort, javaPort);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void startServer(int websocketPort, int javaPort) throws IOException, ExecutionException, InterruptedException {

        GamePieces.register();
        Tiles.setInstance(new Tiles(GameSettings.TILE_TEXTURE_SIZE, false));
        GameSettings.runnablePoster = Gdx.app::postRunnable;
        GameSettings.registerSerializers();


        PacketRegistryContainer registryContainer = new SinglePacketRegistryContainer();
        registryContainer.getDefaultRegistry()
                .registerNetworkConditionPackets()
                .registerDefaultPrimitivePackets()
                .register(TrackChunkPacket::new, PacketHandlers::trackChunkHandle, TrackChunkPacket.class, 0)
                .register(ChunkPacket::new, null, ChunkPacket.class, 1)
                .register(JoinGamePacket::new, PacketHandlers::joinGameHandle, JoinGamePacket.class, 2)
                .register(GameInitPacket::new, null, GameInitPacket.class, 3)
                .register(GamePieceSpawnPacket::new, null, GamePieceSpawnPacket.class, 4)
                .register(GamePieceDespawnPacket::new, null, GamePieceDespawnPacket.class, 5)
                .register(SyncPacket::new, null, SyncPacket.class, 6)
                .register(PropertyPacket::new, null, PropertyPacket.class, 7)
                .register(RequestPropertyNamePacket::new, PacketHandlers::handleRequestPropertyNamePacket, RequestPropertyNamePacket.class, 8)
                .register(PropertyNamePacket::new, null, PropertyNamePacket.class, 9)
                .register(MaintainConnectionPacket::new, null, MaintainConnectionPacket.class, 10)
                .register(PingPacket::new, (ctx) -> ctx.channel.sendNow(ctx.packet.setResponsePacket(ctx.packet), null, ctx.packetType), PingPacket.class, 11)
                .register(TroopGroupPacket::new, PacketHandlers::handleTroopGroupPacket, TroopGroupPacket.class, 12)
                .register(RequestTilePacket::new, PacketHandlers::handleRequestTileTexture, RequestTilePacket.class, 13)
                .register(TilePacket::new, null, TilePacket.class, 14);

        NetworkData networkData = new DefaultNetworkData(Network.PACKET_ENCODER_DECODER, registryContainer);
        ChannelInitializer channelInitializer = channel -> {
            channel.setChannelOption(ChannelOption.TCP_AUTO_FLUSH, true);
            channel.setChannelOption(ChannelOption.UDP_AUTO_FLUSH, true);
            channel.setChannelOption(ChannelOption.PACKET_TIMEOUT, TimeUnit.SECONDS.toNanos(5));
            channel.setChannelOption(ChannelOption.DEFAULT_SWITCH_PROTOCOL, true);
        };
        ServerListener serverListener = new AbstractServerListener() {
            @Override
            public void onClientConnect(Server server, Channel channel) {
                System.out.println("Client connected " + System.identityHashCode(channel) + " : " + Arrays.toString(channel.getAddress()) + " : " + channel.getPort());

            }

            @Override
            public void onClientDisconnect(Server server, Channel channel) {
                System.out.println("Client disconnected " + System.identityHashCode(channel) + " : " + Arrays.toString(channel.getAddress()) + " : " + channel.getPort());
            }

            @Override
            public void onReceived(PacketHandlerContext<?> context) {
                if (commandHandler.watchedPackets.contains(context.packet.getClass())) {
                    System.out.println("Received packet: " + context.packet + " from " + Arrays.toString(context.channel.getAddress()) + " on " + context.packetType);
                }
                if(context.packet instanceof NetworkConditionPackets.EnableNetworkConditionPacket) {
                    System.out.println("Enabling Network Condition: " + ((NetworkConditionPackets.EnableNetworkConditionPacket) context.packet).value);
                }
                if(context.packet instanceof NetworkConditionPackets.LatencyPacket) {
                    System.out.println("Setting Latency: " + ((NetworkConditionPackets.LatencyPacket) context.packet).value);
                }
            }

            @Override
            public void onSent(PacketHandlerContext<?> context) {
                if (commandHandler.watchedPackets.contains(context.packet.getClass())) {
                    System.out.println("Sent packet: " + context.packet + " from " + Arrays.toString(context.channel.getAddress()) + " on " + context.packetType);
                }
            }
        };

        // start websocket server
        websocketServer = new WebsocketServer(RestAPI.getDefaultExecutor(), networkData, bufferFactory, websocketPort); // networkFactory.createServer(networkData, bufferFactory, port);
        // TODO: Update the WebsocketServer code sot hat it has packet timeout functionality and potentially other things it is currently missing
        websocketServer.setChannelInitializer(channelInitializer);
        websocketServer.addServerListener(serverListener);
        setSSL((WebsocketServer) websocketServer);
        websocketServer.startTCP().onFailure(Throwable::printStackTrace).perform().get();
        System.out.println("WebsocketServer started on " + websocketServer.getPort());

        // start java server
        javaServer = networkFactory.createServer(networkData, bufferFactory, javaPort);
        javaServer.setChannelInitializer(channelInitializer);
        javaServer.addServerListener(serverListener);
        javaServer.startTCP().perform().get();
        javaServer.startUDP().perform().get();
        System.out.println("JavaServer started on " + javaServer.getPort());

        field = new ServerField(websocketServer, new ServerChunkLoader(GameSettings.CHUNK_SIZE), GameSettings.CHUNK_SIZE);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                field.addChunk(i, j);
            }
        }
        field.tick(1F);
        System.out.println("Initialized Field");



        commandHandler = new CommandHandler(field);
        new Thread(commandHandler).start();
        System.out.println("Command Handler Started");
    }

    private void setSSL(WebsocketServer websocketServer) {
        if (keystorePassword == null) {
            System.out.println("No keystore password provided, not setting SSL");
            return;
        }
        try {
            final String password = keystorePassword;
            final char[] passwordChars = password.toCharArray();

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("cert.jks"), passwordChars);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, passwordChars);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keyStore);


            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            websocketServer.setWebsocketSocketServerFactory(new DefaultSSLWebSocketServerFactory(sslContext));
            System.out.println("SSL Set");
        } catch (Exception e) {
            System.err.println("Failed to set SSL");
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        try {
            field.tick(Gdx.graphics.getDeltaTime());

            javaServer.checkTCPPackets();
            javaServer.checkUDPPackets();
            websocketServer.checkTCPPackets();
            websocketServer.checkUDPPackets();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            javaServer.close().perform().get();
            websocketServer.close().perform().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            e.printStackTrace();
        }
    }
}
