package com.hirshi001.game.server;

import com.badlogic.gdx.math.MathUtils;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.game.PlayerData;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.game.shared.util.props.Properties;
import com.hirshi001.networking.network.server.Server;

import java.util.*;

public class ServerField extends Field {

    private ChunkLoader loader;
    public Server server;
    public Set<PlayerData> players = Collections.synchronizedSet(new HashSet<>());
    private int nextControllerId = 0;


    public ServerField(Server server, ChunkLoader loader, float cellSize, int chunkSize) {
        super(cellSize, chunkSize);
        this.server = server;
        this.loader = loader;
    }

    public int getNextControllerId() {
        return nextControllerId++;
    }


    @Override
    public Chunk loadChunk(int x, int y) {
        Chunk chunk = loader.loadChunk(getChunkSize(), new HashedPoint(x, y));
        if (chunk != null) chunk.field = this;
        return chunk;
    }

    @Override
    protected void add0(GamePiece gamePiece, int i) {
        super.add0(gamePiece, i);
        ServerChunk chunk = (ServerChunk) gamePiece.chunk;
        for (PlayerData playerData : chunk.trackers) {
            playerData.channel.sendTCP(new GamePieceSpawnPacket(gamePiece), null).perform();
        }
    }

    @Override
    protected boolean remove0(GamePiece gamePiece) {
        ServerChunk serverChunk = (ServerChunk) gamePiece.chunk;
        for (PlayerData playerData : serverChunk.trackers) {
            playerData.channel.sendTCP(new GamePieceDespawnPacket(gamePiece.getGameId()), null).perform();
        }
        for (PlayerData playerData : serverChunk.softTrackers) {
            playerData.channel.sendTCP(new GamePieceDespawnPacket(gamePiece.getGameId()), null).perform();
        }


        return super.remove0(gamePiece);
    }

    @Override
    public Chunk relocateGamePiece(GamePiece item, Chunk original) {
        Chunk newChunk = super.relocateGamePiece(item, original);
        if (newChunk == original) return newChunk;
        if (newChunk == null) return null;
        ServerChunk n = (ServerChunk) newChunk;
        ServerChunk o = (ServerChunk) original;

        for (PlayerData npd : n.trackers) {
            if (!o.trackers.contains(npd)) npd.channel.sendTCP(new GamePieceSpawnPacket(item), null).perform();
        }
        for (PlayerData opd : o.trackers) {
            if (!n.trackers.contains(opd))
                opd.channel.sendTCP(new GamePieceDespawnPacket(item.getGameId()), null).perform();
        }
        return newChunk;
    }

    double dt = 0;

    @Override
    public void tick(float delta) {
        // Test Code:
        dt += delta;
        if (dt >= 10) {
            dt = 0;
            for (PlayerData playerData : players) {
                for (TroopGroup troopGroup : playerData.troopGroups.values()) {
                    troopGroup.moveTroops(MathUtils.random(-20, 10), MathUtils.random(-20, 20), 2);
                }
            }
        }


        Iterator<PlayerData> iterator = players.iterator();
        while (iterator.hasNext()) {
            PlayerData playerData = iterator.next();
            if (playerData.channel.isClosed()) {
                for (Point point : playerData.trackedChunks) {
                    ServerChunk chunk = (ServerChunk) getChunk(point);
                    chunk.trackers.remove(playerData);
                    chunk.softTrackers.remove(playerData);
                }
                iterator.remove();
            }
        }
        super.tick(delta);

        for (GamePiece gamePiece : getItems()) {
            ServerChunk chunk = (ServerChunk) gamePiece.chunk;
            if (!gamePiece.isStatic()) {
                for (PlayerData playerData : chunk.trackers) {
                    playerData.channel.sendUDP(new SyncPacket(time, gamePiece), null).perform();
                }
                for (PlayerData playerData : chunk.softTrackers) {
                    playerData.channel.sendUDP(new SyncPacket(time, gamePiece), null).perform();
                }
            }

            Properties properties = gamePiece.getProperties();
            List<String> modified = properties.getModifiedProperties();
            for (String key : modified) {
                if (properties.getId(key) == null) continue;
                for (PlayerData playerData : chunk.trackers)
                    playerData.channel.sendTCP(new PropertyPacket(gamePiece.getGameId(), properties.getId(key), properties.get(key)), null).perform();
                for (PlayerData playerData : chunk.softTrackers)
                    playerData.channel.sendTCP(new PropertyPacket(gamePiece.getGameId(), properties.getId(key), properties.get(key)), null).perform();
            }
            modified.clear();
        }
    }
}
