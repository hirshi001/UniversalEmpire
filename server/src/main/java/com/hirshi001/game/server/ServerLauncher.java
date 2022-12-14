package com.hirshi001.game.server;

import com.badlogic.gdx.utils.Array;
import com.hirshi001.buffer.bufferfactory.BufferFactory;
import com.hirshi001.buffer.bufferfactory.DefaultBufferFactory;
import com.hirshi001.game.shared.entities.GamePieces;
import com.hirshi001.game.shared.entities.Player;
import com.hirshi001.game.shared.entities.TestGamePiece;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.settings.Network;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.game.shared.util.ByteCounterPacketEncoderDecoder;
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
import com.hirshi001.restapi.ScheduledExec;
import com.hirshi001.websocketnetworkingserver.WebsocketServer;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Launches the server application.
 */
public class ServerLauncher {

    public static NetworkFactory networkFactory;
    public static BufferFactory bufferFactory;
    public static ServerField field;
    public static ScheduledExecutorService executorService;
    public static final Array<Runnable> runnables = new Array<Runnable>(), executingRunnables = new Array<Runnable>();

    public static void main(String[] args) throws Exception {

        int websocketPort, javaPort;
        if (args.length >= 1) {
			websocketPort = Integer.parseInt(args[0]);
        }else{
            websocketPort = 80;
        }
        if (args.length >= 2) {
            javaPort = Integer.parseInt(args[1]);
        }else{
            javaPort = 3000;
        }

        RestAPI.setFactory(new JavaRestFutureFactory());
        executorService = Executors.newScheduledThreadPool(3);
        networkFactory = new JavaNetworkFactory(executorService);
        bufferFactory = new DefaultBufferFactory();
        GameSettings.BUFFER_FACTORY = bufferFactory;
        startServer(websocketPort, javaPort);

    }

    public static void startServer(int websocketPort, int javaPort) throws IOException, ExecutionException, InterruptedException {


        Tiles.register();
        GamePieces.register();
        GameSettings.runnablePoster = ServerLauncher::postRunnable;
        GameSettings.registerSerializers();


        PacketRegistryContainer registryContainer = new SinglePacketRegistryContainer();
        registryContainer.getDefaultRegistry().registerDefaultPrimitivePackets()
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
                .register(PlayerMovePacket::new, PacketHandlers::handlePlayerMovePacket, PlayerMovePacket.class, 11)
                .register(PingPacket::new, (ctx) -> {
                    ctx.channel.send(ctx.packet.setResponsePacket(ctx.packet), null, ctx.packetType).perform();
                }, PingPacket.class, 12)
                .register(ShootPacket::new, PacketHandlers::handleShootPacket, ShootPacket.class, 13);


        NetworkData networkData = new DefaultNetworkData(Network.PACKET_ENCODER_DECODER, registryContainer);

        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            public void initChannel(Channel channel) {
                channel.setChannelOption(ChannelOption.TCP_AUTO_FLUSH, true);
                channel.setChannelOption(ChannelOption.UDP_AUTO_FLUSH, true);
                channel.setChannelOption(ChannelOption.PACKET_TIMEOUT, TimeUnit.SECONDS.toMillis(10));
                channel.setChannelOption(ChannelOption.DEFAULT_SWITCH_PROTOCOL, true);
            }
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
                // System.out.println("Received packet " + context.packet + " from " + context.channel);
            }

            @Override
            public void onSent(PacketHandlerContext<?> context) {
                // System.out.println("Sent packet " + context.packet + " to " + context.channel);
            }
        };

        WebsocketServer websocketServer = new WebsocketServer(new ScheduledExec() {
            @Override
            public void run(Runnable runnable, long delay) {
                executorService.schedule(runnable, delay, TimeUnit.MILLISECONDS);
            }

            @Override
            public void run(Runnable runnable, long delay, TimeUnit period) {
                executorService.schedule(runnable, delay, period);
            }

            @Override
            public void runDeferred(Runnable runnable) {
                executorService.execute(runnable);
            }
        }, networkData, bufferFactory, websocketPort); // networkFactory.createServer(networkData, bufferFactory, port);

        websocketServer.setChannelInitializer(channelInitializer);
        websocketServer.addServerListener(serverListener);
        setSSL(websocketServer);
        websocketServer.startTCP().onFailure(Throwable::printStackTrace).perform().get();

        System.out.println("WebsocketServer started on " + websocketServer.getPort());

        Server javaServer = networkFactory.createServer(networkData, bufferFactory, javaPort);
        javaServer.setChannelInitializer(channelInitializer);
        javaServer.addServerListener(serverListener);
        javaServer.startTCP().perform().get();
        javaServer.startUDP().perform().get();

        System.out.println("JavaServer started on " + javaServer.getPort());

        field = new ServerField(websocketServer, new ServerChunkLoader(), GameSettings.CELL_SIZE, GameSettings.CHUNK_SIZE);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                field.addChunk(i, j);
            }
        }
        field.tick(1F);
        for(int i=0;i<6;i++) field.addGamePiece(new Player());

        Timer timer = new Timer();
        final long period = TimeUnit.SECONDS.toMillis(1) / GameSettings.TICKS_PER_SECOND;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    synchronized (runnables) {
                        executingRunnables.clear();
                        executingRunnables.addAll(runnables);
                        runnables.clear();
                    }
                    for (Runnable runnable : executingRunnables) {
                        runnable.run();
                    }
                    field.tick(GameSettings.SECONDS_PER_TICK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int bytesSent = Network.PACKET_ENCODER_DECODER.encodedBytes.getAndSet(0);
                float bytesPerSecond = bytesSent * GameSettings.TICKS_PER_SECOND;
                //System.out.println("Bytes per second " + bytesPerSecond);
                int maxPacketSze = Network.PACKET_ENCODER_DECODER.maxPacketSize.getAndSet(0);
                //System.out.println("Max packet size = " + maxPacketSze);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, period);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            String[] args = line.split(" ");
            try {
                performCommand(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void setSSL(WebsocketServer websocketServer){
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("keystore.jks"), "password".toCharArray());


            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            websocketServer.setWebsocketSocketServerFactory(new DefaultSSLWebSocketServerFactory(sslContext));
        } catch (Exception e) {
            System.err.println("Failed to set SSL");
        }
    }

    private static void performCommand(String[] args) {
        if (args.length == 0) return;
        String command = args[0];
        if (command.equalsIgnoreCase("exit")) {
            System.exit(0);
        }
        if (command.equalsIgnoreCase("chunk")) {
            chunkCommand(args);
        }
        if (command.equalsIgnoreCase("spawn")) {
            spawnCommand(args);
        }
        if (command.equalsIgnoreCase("setProp")) {
            setPropertyCommand(args);
        }
        if(command.equalsIgnoreCase("remove")) {
            removeCommand(args);
        }
    }

    private static void chunkCommand(String[] args) {
        if (args.length != 3) return;
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);

        Chunk chunk = field.addChunk(x, y);
        if (chunk == null) System.out.println("Chunk is null");
        Tile[][] tiles = chunk.getTiles();
        System.out.println("Chunk " + x + "," + y + ":");
        System.out.println("Tiles: ");
        for (int i = 0; i < GameSettings.CHUNK_SIZE; i++) {
            for (int j = 0; j < GameSettings.CHUNK_SIZE; j++) {
                System.out.print(tiles[j][i].getID() + " ");
            }
            System.out.println();
        }
        System.out.println("GamePieces: ");
        for (GamePiece piece : chunk.items) {
            System.out.println(piece.getClass().getSimpleName() + ": " + piece.getGameId());
        }
    }

    private static void spawnCommand(String[] args) {
        if (args.length != 6) return;
        String name = args[1];
        float x = Float.parseFloat(args[2]);
        float y = Float.parseFloat(args[3]);
        float width = Float.parseFloat(args[4]);
        float height = Float.parseFloat(args[5]);
        GamePiece piece;
        if (name.equals("TestGamePiece")) {
            piece = new TestGamePiece();
            piece.bounds.set(x, y, width, height);
        } else {
            System.out.println("Unknown game piece: " + name);
            return;
        }

        field.addGamePiece(piece);
        System.out.println("Spawned " + piece.getClass().getSimpleName() + ": " + piece.getGameId());
    }

    private static void setPropertyCommand(String[] args) {
        if (args.length < 4) return;
        int gamePieceId = Integer.parseInt(args[1]);
        String property = args[2];

        StringBuilder valueBuilder = new StringBuilder();
        for (int i = 3; i < args.length - 1; i++) {
            valueBuilder.append(args[i]).append(" ");
        }
        valueBuilder.append(args[args.length - 1]);
        String value = valueBuilder.toString();

        Object v = null;
        if (value.equals("true")) v = true;
        else if (value.equals("false")) v = false;
        else {
            try {
                v = Integer.parseInt(value);
            } catch (Exception ignored) {
            }
            if (v == null) {
                try {
                    v = Float.parseFloat(value);
                } catch (Exception ignored) {
                }
            }
            if (v == null) {
                v = value;
            }
        }

        GamePiece piece = field.getGamePiece(gamePieceId);
        if (piece == null) {
            System.out.println("Game piece not found: " + gamePieceId);
            return;
        }
        final Object vf = v;
        GameSettings.runnablePoster.postRunnable(() -> {
            piece.getProperties().put(property, vf);
        });
    }

    private static void removeCommand(String[] args) {
        if (args.length != 2) return;
        int gamePieceId = Integer.parseInt(args[1]);
        GamePiece piece = field.getGamePiece(gamePieceId);
        if (piece == null) {
            System.out.println("Game piece not found: " + gamePieceId);
            return;
        }
        field.removeGamePiece(piece);
    }


    public static void postRunnable(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
        }
    }
}
