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
import com.hirshi001.javanetworking.JavaNetworkFactory;
import com.hirshi001.networking.network.NetworkFactory;
import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.network.channel.ChannelInitializer;
import com.hirshi001.networking.network.channel.ChannelOption;
import com.hirshi001.networking.network.server.AbstractServerListener;
import com.hirshi001.networking.network.server.Server;
import com.hirshi001.networking.networkdata.DefaultNetworkData;
import com.hirshi001.networking.networkdata.NetworkData;
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;
import com.hirshi001.networking.packetregistrycontainer.SinglePacketRegistryContainer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/** Launches the server application. */
public class ServerLauncher {

	public static NetworkFactory networkFactory;
	public static BufferFactory bufferFactory;
	public static ServerField field;
	public static ScheduledExecutorService executorService;
	public static final Array<Runnable> runnables = new Array<Runnable>(), executingRunnables = new Array<Runnable>();

	public static void main(String[] args) throws Exception{
		executorService = Executors.newScheduledThreadPool(3);
		networkFactory = new JavaNetworkFactory(executorService);
		bufferFactory = new DefaultBufferFactory();
		GameSettings.BUFFER_FACTORY = bufferFactory;
		startServer();

	}

	public static void startServer() throws IOException, ExecutionException, InterruptedException {
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
				.register(SyncPacket::new, PacketHandlers::handleSyncPacket, SyncPacket.class, 6)
				.register(PropertyPacket::new, null, PropertyPacket.class, 7)
				.register(RequestPropertyNamePacket::new, PacketHandlers::handleRequestPropertyNamePacket, RequestPropertyNamePacket.class, 8)
				.register(PropertyNamePacket::new, null, PropertyNamePacket.class, 9)
				.register(SoftTrackChunkPacket::new, PacketHandlers::handleSoftTrackChunkPacket, SoftTrackChunkPacket.class, 10);


		NetworkData networkData = new DefaultNetworkData(Network.PACKET_ENCODER_DECODER, registryContainer);

		Server server = networkFactory.createServer(networkData, bufferFactory, Network.PORT);
		server.setChannelInitializer(new ChannelInitializer() {
			@Override
			public void initChannel(Channel channel) {
				channel.setChannelOption(ChannelOption.TCP_AUTO_FLUSH, true);
				channel.setChannelOption(ChannelOption.UDP_AUTO_FLUSH, true);
				channel.setChannelOption(ChannelOption.PACKET_TIMEOUT, (int)TimeUnit.SECONDS.toMillis(10));
			}
		});

		server.addServerListener(new AbstractServerListener() {
			@Override
			public void onClientConnect(Server server, Channel channel) {
				System.out.println("Client connected " + System.identityHashCode(channel) + " : " + Arrays.toString(channel.getAddress()) + " : " + channel.getPort());

			}
			@Override
			public void onClientDisconnect(Server server, Channel channel) {
				System.out.println("Client disconnected " + System.identityHashCode(channel) + " : " + Arrays.toString(channel.getAddress()) + " : " + channel.getPort());
			}

		});


		server.startTCP().perform().get();
		server.startUDP().perform().get();

		field = new ServerField(server, new ServerChunkLoader(), GameSettings.CELL_SIZE, GameSettings.CHUNK_SIZE);
		for(int i=-2;i<=2;i++){
			for(int j=-2;j<=2;j++){
				field.addChunk(i, j);
			}
		}
		field.tick(1F);
		System.out.println("Server started");

		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if(Math.random()<0.1) field.addGamePiece(new Player());
				try{
					synchronized (runnables) {
						executingRunnables.clear();
						executingRunnables.addAll(runnables);
						runnables.clear();
					}
					for(Runnable runnable : executingRunnables){
						runnable.run();
					}
					field.tick(GameSettings.SECONDS_PER_TICK);
				}catch (Exception e){e.printStackTrace();}
			}
		};
		long period = TimeUnit.SECONDS.toMillis(1)/GameSettings.TICKS_PER_SECOND;
		timer.scheduleAtFixedRate(timerTask, 0, period);

		Scanner scanner = new Scanner(System.in);
		while(true){
			String line = scanner.nextLine();
			String[] args = line.split(" ");
			try {
				performCommand(args);
			}catch (Exception e){e.printStackTrace();}
		}
	}

	private static void performCommand(String[] args){
		if(args.length==0) return;
		String command = args[0];
		if(command.equalsIgnoreCase("exit")){
			System.exit(0);
		}
		if(command.equalsIgnoreCase("chunk")){
			chunkCommand(args);
		}
		if(command.equalsIgnoreCase("spawn")){
			spawnCommand(args);
		}
	}

	private static void chunkCommand(String[] args){
		if(args.length!=3) return;
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);

		Chunk chunk = field.addChunk(x, y);
		if(chunk==null) System.out.println("Chunk is null");
		Tile[][] tiles = chunk.getTiles();
		System.out.println("Chunk " + x + "," + y + ":");
		System.out.println("Tiles: ");
		for(int i=0;i<GameSettings.CHUNK_SIZE;i++){
			for(int j=0;j<GameSettings.CHUNK_SIZE;j++){
				System.out.print(tiles[j][i].getID() + " ");
			}
			System.out.println();
		}
		System.out.println("GamePieces: ");
		for(GamePiece piece:chunk.items){
			System.out.println(piece.getClass().getSimpleName() + ": " + piece.getGameId());
		}
	}

	private static void spawnCommand(String[] args){
		if(args.length!=6) return;
		String name = args[1];
		float x = Float.parseFloat(args[2]);
		float y = Float.parseFloat(args[3]);
		float width = Float.parseFloat(args[4]);
		float height = Float.parseFloat(args[5]);
		GamePiece piece;
		if(name.equals("TestGamePiece")){
			piece = new TestGamePiece();
			piece.bounds.set(x, y, width, height);
		}else{
			System.out.println("Unknown game piece: " + name);
			return;
		}

		field.addGamePiece(piece);
		System.out.println("Spawned " + piece.getClass().getSimpleName() + ": " + piece.getGameId());
	}




	public static void postRunnable(Runnable runnable){
		synchronized (runnables) {
			runnables.add(runnable);
		}
	}
}