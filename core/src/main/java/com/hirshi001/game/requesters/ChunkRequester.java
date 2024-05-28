package com.hirshi001.game.requesters;

import com.badlogic.gdx.Gdx;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.packets.TrackChunkPacket;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.packethandlercontext.PacketType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChunkRequester {


    private final Map<Point, Long> chunkLastRequested = new HashMap<>();
    private final HashedPoint point = new HashedPoint();

    private final Channel channel;

    public ChunkRequester(Channel channel) {
        this.channel = channel;
    }



    public void addRequest(int x, int y) {
        point.set(x, y);
        point.recalculateHash();
        int chunkRequestTime = 1;
        if (chunkLastRequested.containsKey(point)) {
            long lastRequestTime = chunkLastRequested.get(point);
            if (System.nanoTime() - lastRequestTime < TimeUnit.SECONDS.toNanos(chunkRequestTime)) {
                return;
            }
        }
        chunkLastRequested.put(new HashedPoint(point.x, point.y), System.nanoTime());
        try {
            channel.sendDeferred(new TrackChunkPacket(point.x, point.y), null, PacketType.TCP);
        }catch (Exception e){
            Gdx.app.getApplicationLogger().error("TrackChunkPacket", "Error sending TrackChunkPacket", e);
        }
    }

    public void removeRequest(int x, int y) {
        point.set(x, y);
        point.recalculateHash();
        chunkLastRequested.remove(point);
    }
}
