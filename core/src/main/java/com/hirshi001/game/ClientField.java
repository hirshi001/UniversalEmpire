package com.hirshi001.game;

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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ClientField extends Field {

    private final Client client;
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

    public ClientField(Client client, float cellSize, int chunkSize) {
        super(cellSize, chunkSize);
        this.client = client;
    }

    public void setFieldRender(FieldRender fieldRender) {
        this.fieldRender = fieldRender;
    }

    /**
     * Sends a request to the server to create a new troop group. The request may not be accepted by the server, but if
     * it is, the server will send a TroopGroupPacket with the newly created troop group.
     *
     * @param playerId the id of the player who is creating the troop group - must be this players id
     * @param name     the name of the troop group
     * @param troopIds the ids of the troops to add to the group
     */
    @Override
    public void createTroopGroup(int playerId, String name, Array<Integer> troopIds) {
        if (playerId != getControllerId()) return;
        client.getChannel().send(new TroopGroupPacket(TroopGroupPacket.OperationType.CREATE, name, troopIds), null, PacketType.TCP).perform();
    }

    /**
     * Sends a request to the server to delete a troop group. The request may not be accepted by the server, but if it
     * is, the server will send a TroopGroupPacket with the deleted troop group.
     *
     * @param playerId the id of the player who is deleting the troop group - must be this players id
     * @param name     the name of the troop group
     */
    @Override
    public void deleteTroopGroup(int playerId, String name) {
        if (playerId != getControllerId()) return;
        client.getChannel().send(new TroopGroupPacket(TroopGroupPacket.OperationType.DELETE, name, null), null, PacketType.TCP).perform();
    }

    /**
     * Sends a request to the server to add troops to a troop group. The request may not be accepted by the server, but
     * if it is, the server will send a TroopGroupPacket with the updated troop group.
     *
     * @param playerId the id of the player who is adding the troops - must be this players id
     * @param name     the name of the troop group
     * @param troopIds the ids of the troops to add to the group
     */
    @Override
    public void addTroopsToGroup(int playerId, String name, Array<Integer> troopIds) {
        if (playerId != getControllerId()) return;
        client.getChannel().send(new TroopGroupPacket(TroopGroupPacket.OperationType.ADD, name, troopIds), null, PacketType.TCP).perform();
    }

    /**
     * Sends a request to the server to remove troops from a troop group. The request may not be accepted by the server,
     * but if it is, the server will send a TroopGroupPacket with the updated troop group.
     *
     * @param playerId the id of the player who is removing the troops - must be this players id
     * @param name     the name of the troop group
     * @param troopIds the ids of the troops to remove from the group
     */
    @Override
    public void removeTroopsFromGroup(int playerId, String name, Array<Integer> troopIds) {
        client.getChannel().send(new TroopGroupPacket(TroopGroupPacket.OperationType.REMOVE, name, troopIds), null, PacketType.TCP).perform();
    }

    @Override
    public TroopGroup getTroopGroup(int playerId, String name) {
        if (playerId == getControllerId()) {
            return playerData.troopGroups.get(name);
        }
        return null;
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
            maintainConnection();
        }

        handleChunkLoading(delta);
        super.tick(delta);
    }

    protected void maintainConnection() {
        if (client.supportsUDP())
            client.getChannel().sendDeferred(new MaintainConnectionPacket(), null, PacketType.UDP);
        else
            client.getChannel().sendDeferred(new MaintainConnectionPacket(), null, PacketType.TCP);
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
