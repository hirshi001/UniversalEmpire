package com.hirshi001.game.server;

import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.troop.Knight;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.game.PlayerData;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.props.Properties;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.packethandlercontext.PacketType;

import java.util.concurrent.ThreadLocalRandom;

public class PacketHandlers {

    public static void trackChunkHandle(PacketHandlerContext<TrackChunkPacket> ctx) {
        PlayerData playerData = (PlayerData) ctx.channel.getAttachment();
        HashedPoint point = new HashedPoint(ctx.packet.chunkX, ctx.packet.chunkY);
        Field field = ServerLauncher.field;

        GameSettings.runnablePoster.postRunnable(() -> {
            Chunk chunk = field.getChunk(point);
            if (chunk == null) {
                chunk = field.addChunk(point.x, point.y);
            }
            if (!ctx.packet.untrack) {
                playerData.trackedChunks.add(point);
                ((ServerChunk) chunk).trackers.add(playerData);
                ctx.channel.sendTCP(new ChunkPacket(chunk), null).perform();
            } else {
                playerData.trackedChunks.remove(point);
                ((ServerChunk) chunk).trackers.remove(playerData);
            }
        });

    }

    public static void joinGameHandle(PacketHandlerContext<JoinGamePacket> ctx) {
        ServerField field = ServerLauncher.field;

        PlayerData playerData = new PlayerData();
        playerData.field = field;
        playerData.channel = ctx.channel;
        playerData.controllerId = field.getNextControllerId();

        ctx.channel.attach(playerData);

        field.players.put(playerData.controllerId, playerData);
        ctx.channel.sendTCP(new GameInitPacket(playerData.controllerId).setResponsePacket(ctx.packet), null).perform();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 20; i++) {
            Knight knight = new Knight();
            knight.setControllerId(playerData.controllerId);
            knight.bounds.setPosition(random.nextInt(-5, 10), random.nextInt(-5, 10));
            field.addGamePiece(knight);
        }

    }

    public static void handleRequestPropertyNamePacket(PacketHandlerContext<RequestPropertyNamePacket> ctx) {
        ServerField field = ServerLauncher.field;
        RequestPropertyNamePacket packet = ctx.packet;
        GamePiece piece = field.getGamePiece(packet.gamePieceId);
        if (piece == null) return;
        Properties properties = piece.getProperties();
        String name = properties.getKeyName(packet.propertyId);
        Object value = properties.get(name);
        ctx.channel.sendTCP(new PropertyNamePacket(packet.gamePieceId, packet.propertyId, name, value), null).perform();
    }

    public static void handleTroopGroupPacket(PacketHandlerContext<TroopGroupPacket> ctx) {
        ServerField field = ServerLauncher.field;
        TroopGroupPacket packet = ctx.packet;
        PlayerData playerData = (PlayerData) ctx.channel.getAttachment();

        Array<Integer> temp = null;
        if (packet.troopIds != null) {
            temp = new Array<>(packet.troopIds.length);
            for (int i = 0; i < packet.troopIds.length; i++) {
                temp.add(packet.troopIds[i]);
            }
        }

        final Array<Integer> troopIds = temp;

        GameSettings.runnablePoster.postRunnable(() -> {
            try {
                if (packet.type == TroopGroupPacket.OperationType.CREATE) {
                    field.createTroopGroup(playerData.controllerId, packet.name, troopIds);
                } else if (packet.type == TroopGroupPacket.OperationType.ADD) {
                    field.addTroopsToGroup(playerData.controllerId, packet.name, troopIds);
                } else if (packet.type == TroopGroupPacket.OperationType.REMOVE) {
                    field.removeTroopsFromGroup(playerData.controllerId, packet.name, troopIds);
                } else if (packet.type == TroopGroupPacket.OperationType.DELETE) {
                    field.deleteTroopGroup(playerData.controllerId, packet.name);
                    playerData.troopGroups.remove(packet.name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


}
