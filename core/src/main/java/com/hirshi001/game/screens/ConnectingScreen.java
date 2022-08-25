package com.hirshi001.game.screens;

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
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;
import com.hirshi001.networking.packetregistrycontainer.SinglePacketRegistryContainer;
import com.hirshi001.restapi.RestFuture;

import java.util.concurrent.ExecutionException;

public class ConnectingScreen extends GameScreen {

    private RestFuture connectFuture;

    public ConnectingScreen(GameApp gameApp) {
        super(gameApp);
    }

    public void connect(String ip) {
        Client client;
        PacketRegistryContainer packetRegistryContainer = new SinglePacketRegistryContainer();
        packetRegistryContainer.getDefaultRegistry().registerDefaultPrimitivePackets()
                .register(TrackChunkPacket::new, null, TrackChunkPacket.class, 0)
                .register(ChunkPacket::new, PacketHandlers::handleChunkPacket, ChunkPacket.class, 1)
                .register(JoinGamePacket::new, null, JoinGamePacket.class, 2)
                .register(GameInitPacket::new, null, GameInitPacket.class, 3)
                .register(GamePieceSpawnPacket::new, PacketHandlers::handleGamePieceSpawnPacket, GamePieceSpawnPacket.class, 4)
                .register(GamePieceDespawnPacket::new,  PacketHandlers::handleGamePieceDespawnPacket, GamePieceDespawnPacket.class, 5)
                .register(SyncPacket::new, PacketHandlers::handleSyncPacket, SyncPacket.class, 6)
                .register(PropertyPacket::new, PacketHandlers::handlePropertyPacket, PropertyPacket.class, 7)
                .register(RequestPropertyNamePacket::new, null, RequestPropertyNamePacket.class, 8)
                .register(PropertyNamePacket::new, PacketHandlers::handlePropertyNamePacket, PropertyNamePacket.class, 9);
        NetworkData networkData = new DefaultNetworkData(Network.PACKET_ENCODER_DECODER, packetRegistryContainer);
        try {
            client = app.networkFactory.createClient(networkData, app.bufferFactory, ip, Network.PORT);
            client.setChannelInitializer(channel -> {
                channel.setChannelOption(ChannelOption.TCP_AUTO_FLUSH, true);
                channel.setChannelOption(ChannelOption.UDP_AUTO_FLUSH, true);
            });

            client.addClientListener(new AbstractChannelListener(){
                @Override
                public void onTCPConnect(Channel channel) {
                    System.out.println("Connected to server");
                }

                @Override
                public void onTCPDisconnect(Channel channel) {
                    System.out.println("Disconnected from server");
                }

                @Override
                public void onChannelClose(Channel channel) {
                    System.out.println("Channel closed");
                }

            });

            client.startUDP().perform();
            connectFuture = client.startTCP().perform();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(float delta) {

        if(connectFuture.isSuccess()){
            try {
                app.client = (Client) connectFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            app.setScreen(new MainGameScreen(app));
        }


        if(connectFuture.isFailure()){
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
