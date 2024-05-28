package com.hirshi001.game.requesters;

import com.badlogic.gdx.Gdx;
import com.hirshi001.game.shared.packets.RequestTilePacket;
import com.hirshi001.game.shared.packets.TilePacket;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.util.defaultpackets.primitivepackets.BooleanPacket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TileRequester {


    private final Map<Integer, Long> tileLastRequested = new HashMap<>();

    private final Channel channel;

    public TileRequester(Channel channel) {
        this.channel = channel;
    }



    public void addRequest(int id) {
        int tileRequestTime = 1;
        if (tileLastRequested.containsKey(id)) {
            long lastRequestTime = tileLastRequested.get(id);
            if (System.nanoTime() - lastRequestTime < TimeUnit.SECONDS.toNanos(tileRequestTime)) {
                return;
            }
        }
        tileLastRequested.put(id, System.nanoTime());
        try {
            channel.sendTCPWithResponse(new RequestTilePacket(id), null, TimeUnit.SECONDS.toMillis(tileRequestTime))
                    .then(ctx -> {
                        if(ctx.packet instanceof BooleanPacket) {
                            throw new RuntimeException("Failed to get tile texture");
                        }
                    })
                    .then(ctx -> {
                        Gdx.app.postRunnable(() -> {
                            Tiles tiles = Tiles.getInstance();

                            TilePacket packet = (TilePacket) ctx.packet;
                            byte[] bytes = packet.texture;
                            Tile tile = packet.tile;

                            Tile originalTile = tiles.tileRegistry.get(tile.getID());
                            if(originalTile == null) {
                                return;
                            }
                            originalTile.set(tile);
                            tiles.getTileTexture().registerAndSetRegion(originalTile, bytes);
                            // Tiles.getInstance().getTileTexture().setBytes(id, bytes);
                        });
                    }).performAsync();
        }catch (Exception e){
            Gdx.app.getApplicationLogger().error("TileRequester", "Error sending RequestTileTexture", e);
        }
    }

    public void removeRequest(int id) {
        tileLastRequested.remove(id);
    }
}
