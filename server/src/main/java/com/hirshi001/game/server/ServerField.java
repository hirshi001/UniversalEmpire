package com.hirshi001.game.server;

import com.badlogic.gdx.math.MathUtils;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.PlayerData;
import com.hirshi001.game.shared.packets.GamePieceDespawnPacket;
import com.hirshi001.game.shared.packets.GamePieceSpawnPacket;
import com.hirshi001.game.shared.packets.PropertyPacket;
import com.hirshi001.game.shared.packets.SyncPacket;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.game.shared.util.props.Properties;
import com.hirshi001.networking.network.server.Server;
import com.hirshi001.restapi.RestAPI;

import java.util.*;

public class ServerField extends Field {

    private final ChunkLoader loader;
    public Server server;
    public Map<Integer, PlayerData> players = Collections.synchronizedMap(new HashMap<>());
    private int nextControllerId = 0;


    public ServerField(Server server, ChunkLoader loader, int chunkSize) {
        super(chunkSize, new ServerGameMechanics(), RestAPI.getDefaultExecutor());
        this.server = server;
        this.loader = loader;
    }

    public int getNextControllerId() {
        return nextControllerId++;
    }


    @Override
    public Chunk loadChunk(int x, int y) {
        Chunk chunk = loader.loadChunk(new HashedPoint(x, y));
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
            for (PlayerData playerData : players.values()) {
                for (TroopGroup troopGroup : playerData.troopGroups.values()) {
                    getGameMechanics().moveTroopGroup(playerData.controllerId, troopGroup.name, MathUtils.random(-20, 10), MathUtils.random(-20, 20));
                    // troopGroup.moveTroops(MathUtils.random(-20, 10), MathUtils.random(-20, 20), 2);
                }
            }
        }


        Iterator<PlayerData> iterator = players.values().iterator();
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

        // every frame, only a maximum of 10 game pieces can send sync packets in order to prevent flooding
        int piecesToSync = 10;
        for (GamePiece gamePiece : getItems()) {
            if (gamePiece.syncedRecently || !gamePiece.needsSync()) continue;

            ServerChunk chunk = (ServerChunk) gamePiece.chunk;

            for (PlayerData playerData : chunk.trackers) {
                playerData.channel.sendUDP(new SyncPacket(time, gamePiece), null).perform();
            }
            for (PlayerData playerData : chunk.softTrackers) {
                playerData.channel.sendUDP(new SyncPacket(time, gamePiece), null).perform();
            }

            gamePiece.syncedRecently = true;
            piecesToSync--;
            if (piecesToSync <= 0) break;

        }

        for (GamePiece gamePiece : getItems()) {
            if (piecesToSync > 0) {
                gamePiece.syncedRecently = false;
            }

            ServerChunk chunk = (ServerChunk) gamePiece.chunk;
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
