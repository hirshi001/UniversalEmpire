package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.PacketHandlers;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.Network;
import com.hirshi001.networking.network.channel.AbstractChannelListener;
import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.network.channel.ChannelOption;
import com.hirshi001.networking.network.client.Client;
import com.hirshi001.networking.networkdata.DefaultNetworkData;
import com.hirshi001.networking.networkdata.NetworkData;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.packethandlercontext.PacketType;
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;
import com.hirshi001.networking.packetregistrycontainer.SinglePacketRegistryContainer;
import com.hirshi001.restapi.RestFuture;

import java.util.concurrent.ExecutionException;

public class ConnectingScreen extends GameScreen {

    private RestFuture connectFuture;

    public ConnectingScreen(GameApp gameApp) {
        super(gameApp);
    }

    public void connect(String ip, int port) {
        Gdx.app.log("ConnectingScreen", "Connecting to" + ip + ":" + port);
        Client client;
        PacketRegistryContainer packetRegistryContainer = new SinglePacketRegistryContainer();
        packetRegistryContainer.getDefaultRegistry().registerDefaultPrimitivePackets()
                .register(TrackChunkPacket::new, null, TrackChunkPacket.class, 0)
                .register(ChunkPacket::new, PacketHandlers::handleChunkPacket, ChunkPacket.class, 1)
                .register(JoinGamePacket::new, null, JoinGamePacket.class, 2)
                .register(GameInitPacket::new, PacketHandlers::handleGameInitPacket, GameInitPacket.class, 3)
                .register(GamePieceSpawnPacket::new, PacketHandlers::handleGamePieceSpawnPacket, GamePieceSpawnPacket.class, 4)
                .register(GamePieceDespawnPacket::new,  PacketHandlers::handleGamePieceDespawnPacket, GamePieceDespawnPacket.class, 5)
                .register(SyncPacket::new, PacketHandlers::handleSyncPacket, SyncPacket.class, 6)
                .register(PropertyPacket::new, PacketHandlers::handlePropertyPacket, PropertyPacket.class, 7)
                .register(RequestPropertyNamePacket::new, null, RequestPropertyNamePacket.class, 8)
                .register(PropertyNamePacket::new, PacketHandlers::handlePropertyNamePacket, PropertyNamePacket.class, 9)
				.register(MaintainConnectionPacket::new, null, MaintainConnectionPacket.class, 10)
                .register(PlayerMovePacket::new, PacketHandlers::handlePlayerMovePacket, PlayerMovePacket.class, 11)
                .register(PingPacket::new, null, PingPacket.class, 12)
                .register(ShootPacket::new, null, ShootPacket.class, 13);

        NetworkData networkData = new DefaultNetworkData(Network.PACKET_ENCODER_DECODER, packetRegistryContainer);
        try {
            Gdx.app.log("ConnectingScreen", "Creating client");
            GameApp.Game().client = client = app.networkFactory.createClient(networkData, app.bufferFactory, ip, port);
            client.setChannelInitializer(channel -> {
                // channel.setChannelOption(ChannelOption.TCP_AUTO_FLUSH, true);
                channel.setChannelOption(ChannelOption.UDP_AUTO_FLUSH, true);
                channel.setChannelOption(ChannelOption.DEFAULT_SWITCH_PROTOCOL, true);
            });

            client.addClientListener(new AbstractChannelListener(){
                @Override
                public void onTCPConnect(Channel channel) {
                }

                @Override
                public void onTCPDisconnect(Channel channel) {
                    System.out.println("Disconnected from server");
                }

                @Override
                public void onChannelClose(Channel channel) {
                    System.out.println("Channel closed");
                }

                @Override
                public void onSent(PacketHandlerContext<?> context) {
                }

                @Override
                public void onReceived(PacketHandlerContext<?> context) {
                }
            });

            Gdx.app.log("ConnectingScreen", "About to connect server");
            connectFuture = client.startTCP();
            connectFuture.perform();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.RED);
        if(connectFuture.isSuccess()){
            try {
                Gdx.app.log("ConnectingScreen", "TCP Connected to server");
                app.client.startUDP().perform().get();
                Gdx.app.log("ConnectingScreen", "UDP Connected to server");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Gdx.app.log("ConnectingScreen", "Changing Screens");
            app.setScreen(new MainGameScreen(app));
            Gdx.app.log("ConnectingScreen", "Changed Screens");
        }


        if(connectFuture.isFailure()){
            Gdx.app.log("ConnectingScreen", "Failed to connect to server");
            app.setScreen(new ErrorScreen(app, connectFuture.cause()));
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
