package com.hirshi001.game.server;

import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.game.shared.util.props.Properties;
import com.hirshi001.networking.network.server.Server;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ServerField extends Field {

    private ChunkLoader loader;
    public Server server;
    public Set<PlayerData> players = new HashSet<>();


    public ServerField(Server server, ChunkLoader loader, float cellSize, int chunkSize) {
        super(cellSize, chunkSize);
        this.server = server;
        this.loader = loader;
    }

    @Override
    public Chunk loadChunk(int x, int y) {
        Chunk chunk = loader.loadChunk(getChunkSize(), new HashedPoint(x, y));
        if(chunk!=null) chunk.field = this;
        return chunk;
    }

    @Override
    protected void add0(GamePiece gamePiece, int i) {
        super.add0(gamePiece, i);
        ServerChunk chunk = (ServerChunk)gamePiece.chunk;
        for(PlayerData playerData:chunk.trackers){
            playerData.channel.sendTCP(new GamePieceSpawnPacket(gamePiece), null).perform();
        }
    }

    @Override
    public Chunk relocateGamePiece(GamePiece item, Chunk original) {
        Chunk newChunk = super.relocateGamePiece(item, original);
        if(newChunk==original) return newChunk;
        if(newChunk==null) return null;
        ServerChunk n = (ServerChunk) newChunk;
        ServerChunk o = (ServerChunk) original;

        for(PlayerData npd : n.trackers) {
            if(!o.trackers.contains(npd)) npd.channel.sendTCP(new GamePieceSpawnPacket(item), null).perform();
        }
        for(PlayerData opd : o.trackers) {
            if(!n.trackers.contains(opd)) opd.channel.sendTCP(new GamePieceDespawnPacket(item.getGameId()), null).perform();
        }
        return newChunk;
    }

    @Override
    public void tick(float delta) {
        Iterator<PlayerData> iterator = players.iterator();
        while(iterator.hasNext()){
            PlayerData playerData = iterator.next();
            if(playerData.channel.isClosed()){
                for(Point point:playerData.trackedChunks){
                    ServerChunk chunk = (ServerChunk)getChunk(point);
                    chunk.trackers.remove(playerData);
                    chunk.softTrackers.remove(playerData);
                }
                iterator.remove();
            }
        }
        super.tick(delta);

        for(GamePiece gamePiece:getItems()){
            if(gamePiece.isStatic()) continue;
            ServerChunk chunk = (ServerChunk) gamePiece.chunk;

            for(PlayerData playerData:chunk.trackers) playerData.channel.sendUDP(new SyncPacket(tick, gamePiece), null).perform();
            for(PlayerData playerData:chunk.softTrackers) playerData.channel.sendUDP(new SyncPacket(tick, gamePiece), null).perform();

            Properties properties = gamePiece.getProperties();
            List<String> modified = properties.getModifiedProperties();
            for(String key:modified){
                for(PlayerData playerData:chunk.trackers) playerData.channel.sendTCP(new PropertyPacket(gamePiece.getGameId(), properties.getId(key), properties.get(key)), null).perform();
                for(PlayerData playerData:chunk.softTrackers) playerData.channel.sendTCP(new PropertyPacket(gamePiece.getGameId(), properties.getId(key), properties.get(key)), null).perform();

            }
            modified.clear();
        }
    }
}
