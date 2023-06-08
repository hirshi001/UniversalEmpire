package com.hirshi001.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.render.ActorMap;
import com.hirshi001.game.render.FieldRender;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.troop.Troop;
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
import com.hirshi001.networking.packet.Packet;
import com.hirshi001.networking.packethandlercontext.PacketType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ClientField extends Field {

    private final Client client;
    public final PlayerData playerData = new PlayerData();
    private final Map<Point, Long> chunkLastRequested = new HashMap<>();
    private final Map<Chunk, Long> chunkLife = new HashMap<>();

    private final Vector2 position = new Vector2();
    public FieldRender fieldRender;

    public int chunkLoadRadius = 2;

    public Vector2 getPosition() {
        return position;
    }

    public int getControllerId() {
        return playerData.controllerId;
    }

    public ClientField(Client client, float cellSize, int chunkSize) {
        super(cellSize, chunkSize);
        this.client = client;
    }

    public void setFieldRender(FieldRender fieldRender) {
        this.fieldRender = fieldRender;
    }

    public void addTroopGroup(TroopGroup troopGroup) {
        playerData.troopGroups.put(troopGroup, troopGroup);
        Packet packet = new TroopGroupPacket(TroopGroupPacket.OperationType.CREATE, troopGroup.name, troopGroup.getDirtyTroops());
        client.getChannel().sendDeferred(packet, null, PacketType.TCP);
        troopGroup.dirtyTroops.clear();
    }

    public TroopGroup getTroopGroup(String name) {
        return playerData.troopGroups.get(new TroopGroup(this, name, getControllerId()));
    }

    public void addTroopsToGroup(TroopGroup troopGroup) {
        Packet packet = new TroopGroupPacket(TroopGroupPacket.OperationType.ADD, troopGroup.name, troopGroup.getDirtyTroops());
        client.getChannel().sendDeferred(packet, null, PacketType.TCP);
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
        if (chunkLastRequested.containsKey(point)) {
            long lastRequestTime = chunkLastRequested.get(point);
            if (System.currentTimeMillis() - lastRequestTime < TimeUnit.SECONDS.toMillis(1)) {
                return null;
            }
        }
        chunkLastRequested.put(new HashedPoint(point.x, point.y), System.currentTimeMillis());
        client.getChannel().sendDeferred(new TrackChunkPacket(point.x, point.y), null, PacketType.TCP);
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
            if (client.supportsUDP())
                client.getChannel().sendDeferred(new MaintainConnectionPacket(), null, PacketType.UDP);
            else
                client.getChannel().sendDeferred(new MaintainConnectionPacket(), null, PacketType.TCP);
        }

        Point chunkP = getChunkPosition(position.x, position.y);
        int s = chunkLoadRadius;
        for (int i = -s; i <= s; i++) {
            for (int j = -s; j <= s; j++) {
                Chunk chunk = addChunk(chunkP.x + i, chunkP.y + j);
                if (chunk != null) {
                    chunkLife.put(chunk, 0L);
                }
            }
        }
        for (Chunk chunk : chunks.values()) {
            chunkLife.putIfAbsent(chunk, 0L);
        }
        chunkLife.replaceAll((chunk, life) -> life + 1);
        chunkLife.entrySet().removeIf(entry -> {
            if (entry.getValue() > 20) {
                removeChunk(entry.getKey().getChunkX(), entry.getKey().getChunkY());
                return true;
            }
            return false;
        });
        super.tick(delta);

        for (TroopGroup troopGroup : playerData.troopGroups.values()) {
            if (troopGroup.dirtyTroops.size > 0) {
                Packet packet = new TroopGroupPacket(TroopGroupPacket.OperationType.ADD, troopGroup.name, troopGroup.getDirtyTroops());
                client.getChannel().sendDeferred(packet, null, PacketType.TCP);
                troopGroup.dirtyTroops.clear();
            }
        }
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
