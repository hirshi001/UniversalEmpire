package com.hirshi001.game;

import com.hirshi001.game.render.ActorMap;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.packets.SoftTrackChunkPacket;
import com.hirshi001.game.shared.packets.TrackChunkPacket;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.game.shared.util.props.PropertiesManager;
import com.hirshi001.networking.network.client.Client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ClientField extends Field {

    private Client client;
    private Map<HashedPoint, Long> chunkLastRequested = new HashMap<>();
    private Map<Point, Chunk> softLoadedChunks = new HashMap<>();
    private Tile[][] softLoadedTiles = new Tile[GameSettings.CHUNK_SIZE][GameSettings.CHUNK_SIZE];
    private HashedPoint tempHashPoint = new HashedPoint();

    public ClientField(Client client, float cellSize, int chunkSize) {
        super(cellSize, chunkSize);
        this.client = client;
        for(int x = 0; x < GameSettings.CHUNK_SIZE; x++) {
            for(int y = 0; y < GameSettings.CHUNK_SIZE; y++) {
                softLoadedTiles[x][y] = Tiles.DEFAULT_TILE;
            }
        }
    }

    @Override
    protected void add0(GamePiece gamePiece, int i) {
        super.add0(gamePiece, i);
        System.out.println("GamePiece added: " + gamePiece.getGameId());
        gamePiece.userData = ActorMap.get(gamePiece);
    }

    @Override
    public Chunk loadChunk(int x, int y) {
        if(containsChunk(x, y)) return getChunk(x, y);
        HashedPoint point = new HashedPoint(x, y);
        if(chunkLastRequested.containsKey(point)){
            long lastRequestTime = chunkLastRequested.get(point);
            if(System.currentTimeMillis() - lastRequestTime < TimeUnit.SECONDS.toMillis(1)){
                return null;
            }
        }
        chunkLastRequested.put(new HashedPoint(point.x, point.y), System.currentTimeMillis());
        client.sendTCP(new TrackChunkPacket(point.x, point.y), null).perform();
        return null;
    }

    public void softLoadChunk(int x, int y){
        HashedPoint point = new HashedPoint(x, y);
        if(softLoadedChunks.containsKey(point)){
            return;
        }
        Chunk chunk = new Chunk(GameSettings.CHUNK_SIZE, point);
        chunk.softLoaded = true;
        softLoadedChunks.put(point, chunk);
        client.sendTCP(new SoftTrackChunkPacket(point.x, point.y), null).perform();
    }

    @Override
    public void tick(float delta) {
        for(int i=-2;i<=2;i++){
            for(int j=-2;j<=2;j++){
                addChunk(i, j);
            }
        }
        for(int i=-3;i<=3;i++){
            for(int j=-3;j<=3;j++){
                softLoadChunk(i, j);
            }
        }
        super.tick(delta);
    }

    @Override
    public boolean isServer() {
        return false;
    }

    @Override
    public Chunk relocateGamePiece(GamePiece item, Chunk original) {
        Chunk chunk = super.relocateGamePiece(item, original);
        if(chunk!=null) return chunk;
        return getSoftChunk(item.getCenterX(), item.getCenterY());
    }

    public Chunk getSoftChunk(float x, float y){
        getChunkPosition(x, y, tempHashPoint);
        tempHashPoint.recalculateHash();
        return softLoadedChunks.get(tempHashPoint);
    }
}
