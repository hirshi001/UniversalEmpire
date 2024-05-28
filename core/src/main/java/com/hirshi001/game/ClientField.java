package com.hirshi001.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.hirshi001.game.render.ActorMap;
import com.hirshi001.game.render.FieldRender;
import com.hirshi001.game.requesters.ChunkRequester;
import com.hirshi001.game.requesters.TileRequester;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.game.PlayerData;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.networking.network.client.Client;
import com.hirshi001.networking.packethandlercontext.PacketType;
import com.hirshi001.networking.util.defaultpackets.arraypackets.ByteArrayPacket;
import com.hirshi001.networking.util.defaultpackets.primitivepackets.BooleanPacket;
import com.hirshi001.restapi.RestAPI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ClientField extends Field {

    public final Client client;
    public final PlayerData playerData = new PlayerData();
    public int getControllerId() {
        return playerData.controllerId;
    }

    // For requesting data from the server
    public final ChunkRequester chunkRequester;
    public final TileRequester tileRequester;

    // Handling Chunk loading
    public int chunkLoadRadius = 2;
    private final Map<Chunk, Float> chunkLife = new HashMap<>();

    // Position of the camera, used for loading chunks
    private final Vector2 position = new Vector2();
    public Vector2 getPosition() {
        return position;
    }

    // Field Render, needed for updating render maps when entity is added to field
    public FieldRender fieldRender;

    public ClientField(Client client, int chunkSize) {
        super(chunkSize, new ClientGameMechanics(), RestAPI.getDefaultExecutor());
        chunkRequester = new ChunkRequester(client.getChannel());
        tileRequester = new TileRequester(client.getChannel());
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
        chunkRequester.addRequest(x, y);
        return null;
    }

    @Override
    public Chunk addChunk(Chunk chunk) {
        if (chunk == null) return null;
        chunkRequester.removeRequest(chunk.getChunkX(), chunk.getChunkY());
        return super.addChunk(chunk);
    }

    @Override
    public void tick(float delta) {
        if (client.isOpen()) {
            // maintainConnection();
        }
        handleChunkLoading(delta);
        checkTilesToRemove();
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

    // removes tile textures which are not in use, and queues the texture to local file saving
    protected void checkTilesToRemove() {
        Tiles instance = Tiles.getInstance();
        Set<Tile> toRemove = new HashSet<>();
        instance.tileRegistry.get(toRemove);
        for(Chunk chunk : chunks.values()) {
            for(int i = 0; i < chunk.getChunkSize(); i++) {
                for(int j = 0; j < chunk.getChunkSize(); j++) {
                    Tile tile = chunk.getTile(i, j);
                    if(tile == null) continue;
                    toRemove.remove(tile);
                }
            }
        }

        for(Tile tile : toRemove) {
        //    instance.tileRegistry.remove(tile.getID());
        }
    }

    @Override
    public boolean isServer() {
        return false;
    }


    @Override
    public boolean removeChunk(Point chunk) {
        client.getChannel().sendDeferred(new TrackChunkPacket(chunk.x, chunk.y, true), null, PacketType.TCP);
        chunkRequester.removeRequest(chunk.x, chunk.y);
        return super.removeChunk(chunk);
    }

    public void requestTileTexture(final int id) {
        tileRequester.addRequest(id);
    }
}
