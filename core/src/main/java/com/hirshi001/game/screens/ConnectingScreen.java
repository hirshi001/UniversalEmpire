package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.PacketHandlers;
import com.hirshi001.game.screens.maingamescreen.MainGameScreen;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.Network;
import com.hirshi001.networking.network.channel.AbstractChannelListener;
import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.network.channel.ChannelOption;
import com.hirshi001.networking.network.client.Client;
import com.hirshi001.networking.networkdata.DefaultNetworkData;
import com.hirshi001.networking.networkdata.NetworkData;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;
import com.hirshi001.networking.packetregistrycontainer.SinglePacketRegistryContainer;
import com.hirshi001.restapi.RestFuture;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutionException;

public class ConnectingScreen extends GameScreen {

    private RestFuture connectFuture;

    public ConnectingScreen(GameApp gameApp) {
        super(gameApp);
    }

    public void connect(String ip, int port) {

        /*
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl("http://localhost:8080/accounts/login");
        httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded"); // Set the content type accordingly
        httpRequest.setContent("username=hirshi001&password=hirshi001");
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.log("Http Request Login", httpResponse.getResultAsString());
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    // Login Success
                    // other code
                }
            }
            @Override
            public void failed(Throwable t) { }
            @Override
            public void cancelled() {}
        });

         */


        Gdx.app.log("Connecting Screen", "Connecting to " + ip + ":" + port);
        Client client;
        PacketRegistryContainer packetRegistryContainer = new SinglePacketRegistryContainer();
        packetRegistryContainer.getDefaultRegistry().registerDefaultPrimitivePackets()
                .register(TrackChunkPacket::new, null, TrackChunkPacket.class, 0)
                .register(ChunkPacket::new, PacketHandlers::handleChunkPacket, ChunkPacket.class, 1)
                .register(JoinGamePacket::new, null, JoinGamePacket.class, 2)
                .register(GameInitPacket::new, PacketHandlers::handleGameInitPacket, GameInitPacket.class, 3)
                .register(GamePieceSpawnPacket::new, PacketHandlers::handleGamePieceSpawnPacket, GamePieceSpawnPacket.class, 4)
                .register(GamePieceDespawnPacket::new, PacketHandlers::handleGamePieceDespawnPacket, GamePieceDespawnPacket.class, 5)
                .register(SyncPacket::new, PacketHandlers::handleSyncPacket, SyncPacket.class, 6)
                .register(PropertyPacket::new, PacketHandlers::handlePropertyPacket, PropertyPacket.class, 7)
                .register(RequestPropertyNamePacket::new, null, RequestPropertyNamePacket.class, 8)
                .register(PropertyNamePacket::new, PacketHandlers::handlePropertyNamePacket, PropertyNamePacket.class, 9)
                .register(MaintainConnectionPacket::new, null, MaintainConnectionPacket.class, 10)
                .register(PingPacket::new, null, PingPacket.class, 11)
                .register(TroopGroupPacket::new, PacketHandlers::handleTroopGroupPacket, TroopGroupPacket.class, 12);

        NetworkData networkData = new DefaultNetworkData(Network.PACKET_ENCODER_DECODER, packetRegistryContainer);
        try {
            Gdx.app.log("Connecting Screen", "Creating client");
            GameApp.client = client = GameApp.networkFactory.createClient(networkData, GameApp.bufferFactory, ip, port);
            client.setChannelInitializer(channel -> {
                // channel.setChannelOption(ChannelOption.TCP_AUTO_FLUSH, true);
                channel.setChannelOption(ChannelOption.UDP_AUTO_FLUSH, true);
                channel.setChannelOption(ChannelOption.DEFAULT_SWITCH_PROTOCOL, true);
            });

            client.addClientListeners(new AbstractChannelListener() {
                @Override
                public void onTCPConnect(Channel channel) {
                }

                @Override
                public void onTCPDisconnect(Channel channel) {
                    Gdx.app.error("Client Listener", "Disconnected from server");
                }

                @Override
                public void onChannelClose(Channel channel) {
                    Gdx.app.error("Client Listener", "Channel closed");
                }

                @Override
                public void onSent(PacketHandlerContext<?> context) {
                    Gdx.app.log("Client Listener", "Packet Sent: " + context.packet.getClass().getName());
                }

                @Override
                public void onReceived(PacketHandlerContext<?> context) {
                }
            });

            Gdx.app.log("Connecting Screen", "About to connect server");
            connectFuture = client.startTCP();
            connectFuture.perform();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.RED);
        if (connectFuture.isSuccess()) {
            try {
                Gdx.app.log("Connecting Screen", "TCP Connected to server");
                GameApp.client.startUDP().perform().get();
                Gdx.app.log("Connecting Screen", "UDP Connected to server");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Gdx.app.log("ConnectingScreen", "Changing Screens");
            app.setScreen(new MainGameScreen(app));
            Gdx.app.log("ConnectingScreen", "Changed Screens");
        }


        if (connectFuture.isFailure()) {
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
