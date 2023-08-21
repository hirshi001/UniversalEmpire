package com.hirshi001.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fma.FormationPattern;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.render.ActorMap;
import com.hirshi001.game.render.FieldRender;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.game.PlayerData;
import com.hirshi001.game.shared.packets.MaintainConnectionPacket;
import com.hirshi001.game.shared.packets.TrackChunkPacket;
import com.hirshi001.game.shared.packets.TroopGroupPacket;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.networking.network.client.Client;
import com.hirshi001.networking.packethandlercontext.PacketType;
import com.hirshi001.restapi.RestAPI;
import com.hirshi001.restapi.ScheduledExec;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ClientField extends Field {

    public final Client client;
    public final PlayerData playerData = new PlayerData();
    private final Map<Point, Long> chunkLastRequested = new HashMap<>();
    private final Map<Chunk, Float> chunkLife = new HashMap<>();

    private final Vector2 position = new Vector2();
    public FieldRender fieldRender;

    public int chunkLoadRadius = 2;

    public Vector2 getPosition() {
        return position;
    }

    public int getControllerId() {
        return playerData.controllerId;
    }

    public ClientField(Client client, int chunkSize) {
        super(chunkSize, new ClientGameMechanics(), RestAPI.getDefaultExecutor());
        this.client = client;
    }

    public void setFieldRender(FieldRender fieldRender) {
        this.fieldRender = fieldRender;
    }

    @Override
    protected void add0(GamePiece gamePiece, int i) {
        super.add0(gamePiece, i);
        gamePiece.userData = ActorMap.get(gamePiece, fieldRender);
    }

    @Override
    public Chunk loadChunk(int x, int y) {
        if (containsChunk(x, y)) {
            return getChunk(x, y);
        }

        HashedPoint point = new HashedPoint(x, y);
        int chunkRequestTime = 1;
        if (chunkLastRequested.containsKey(point)) {
            long lastRequestTime = chunkLastRequested.get(point);
            if (System.nanoTime() - lastRequestTime < TimeUnit.SECONDS.toNanos(chunkRequestTime)) {
                return null;
            }
        }
        chunkLastRequested.put(new HashedPoint(point.x, point.y), System.nanoTime());
        try {
            client.getChannel().sendDeferred(new TrackChunkPacket(point.x, point.y), null, PacketType.TCP);
        }catch (Exception e){
            Gdx.app.getApplicationLogger().error("TrackChunkPacket", "Error sending TrackChunkPacket", e);
        }
        return null;
    }

    @Override
    public Chunk addChunk(Chunk chunk) {
        if (chunk == null) return null;
        chunkLastRequested.remove(chunk.chunkPosition);
        return super.addChunk(chunk);
    }

    @Override
    public void tick(float delta) {
        if (client.isOpen()) {
            // maintainConnection();
        }

        handleChunkLoading(delta);
        super.tick(delta);
    }

    MaintainConnectionPacket maintainConnectionPacket = new MaintainConnectionPacket();
    protected void maintainConnection() {
        if (client.supportsUDP())
            client.getChannel().sendDeferred(maintainConnectionPacket, null, PacketType.UDP);
        else
            client.getChannel().sendDeferred(maintainConnectionPacket, null, PacketType.TCP);
    }

    protected void handleChunkLoading(float delta) {
        // determine which chunks should be loaded
        Point chunkP = getChunkPosition(position.x, position.y);
        int s = chunkLoadRadius;
        for (int i = -s; i <= s; i++) {
            for (int j = -s; j <= s; j++) {
                Chunk chunk = addChunk(chunkP.x + i, chunkP.y + j);
                if (chunk != null) {
                    chunkLife.put(chunk, 0F);
                }
            }
        }

        // remove chunks which have not needed to be loaded for a while (2 seconds)
        final float chunkLifeTime = 2F;
        for (Chunk chunk : chunks.values()) {
            chunkLife.putIfAbsent(chunk, 0F);
        }
        chunkLife.replaceAll((chunk, life) -> life + delta);
        chunkLife.entrySet().removeIf(entry -> {
            if (entry.getValue() > chunkLifeTime) {
                removeChunk(entry.getKey().getChunkX(), entry.getKey().getChunkY());
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean isServer() {
        return false;
    }


    @Override
    public boolean removeChunk(Point chunk) {
        client.getChannel().sendDeferred(new TrackChunkPacket(chunk.x, chunk.y, true), null, PacketType.TCP);
        chunkLastRequested.remove(chunk);
        return super.removeChunk(chunk);

    }
}
