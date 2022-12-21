package com.hirshi001.game;

import com.badlogic.gdx.math.Vector2;
import com.hirshi001.game.render.ActorMap;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.packets.MaintainConnectionPacket;
import com.hirshi001.game.shared.packets.TrackChunkPacket;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.networking.network.client.Client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ClientField extends Field {

    private final Client client;

    private final Map<Point, Long> chunkLastRequested = new HashMap<>();
    private final Map<Chunk, Long> chunkLife = new HashMap<>();

    private final Vector2 position = new Vector2();

    public Vector2 getPosition() {
        return position;
    }

    public ClientField(Client client, float cellSize, int chunkSize) {
        super(cellSize, chunkSize);
        this.client = client;
    }

    @Override
    protected void add0(GamePiece gamePiece, int i) {
        super.add0(gamePiece, i);
        System.out.println("GamePiece added: " + gamePiece.getGameId());
        gamePiece.userData = ActorMap.get(gamePiece);
    }

    @Override
    public Chunk loadChunk(int x, int y) {
        if(containsChunk(x, y)){
            return getChunk(x, y);
        }
        HashedPoint point = new HashedPoint(x, y);
        if(chunkLastRequested.containsKey(point)){
            long lastRequestTime = chunkLastRequested.get(point);
            if(System.currentTimeMillis() - lastRequestTime < TimeUnit.SECONDS.toMillis(1)){
                return null;
            }
        }
        chunkLastRequested.put(new HashedPoint(point.x, point.y), System.currentTimeMillis());
        client.getChannel().sendTCP(new TrackChunkPacket(point.x, point.y), null).perform();
        return null;
    }

    @Override
    public Chunk addChunk(Chunk chunk) {
        if(chunk==null) return null;
        chunkLastRequested.remove(chunk.chunkPosition);
        return super.addChunk(chunk);
    }

    @Override
    public void tick(float delta) {
        client.getChannel().sendTCP(new MaintainConnectionPacket(), null).perform();
        Vector2 position = getPosition();
        Point chunkP = getChunkPosition(position.x, position.y);
        int s = 2;
        for(int i=-s;i<=s;i++){
            for(int j=-s;j<=s;j++){
                Chunk chunk = addChunk(chunkP.x + i, chunkP.y + j);
                if(chunk!=null){
                    chunkLife.put(chunk, 0L);
                }
            }
        }
        for(Chunk chunk:chunks.values()){
            chunkLife.putIfAbsent(chunk, 0L);
        }
        chunkLife.replaceAll((chunk, life) -> life + 1);
        chunkLife.entrySet().removeIf(entry -> {
            if(entry.getValue() > 20){
                removeChunk(entry.getKey().getChunkX(), entry.getKey().getChunkY());
                return true;
            }
            return false;
        });
        super.tick(delta);
    }

    @Override
    public boolean isServer() {
        return false;
    }

    @Override
    public Chunk relocateGamePiece(GamePiece item, Chunk original) {
        return super.relocateGamePiece(item, original);
    }

    @Override
    public boolean removeChunk(Point chunk) {
        client.getChannel().sendTCP(new TrackChunkPacket(chunk.x, chunk.y, true), null).perform();
        chunkLastRequested.remove(chunk);
        return super.removeChunk(chunk);

    }
}
