package com.hirshi001.game.server;

import com.hirshi001.game.shared.entities.Fireball;
import com.hirshi001.game.shared.entities.Player;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.props.Properties;
import com.hirshi001.networking.packet.DataPacket;
import com.hirshi001.networking.packet.Packet;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;

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
        playerData.player = new Player();
        playerData.player.bounds.setPosition(1F, 1F);
        playerData.channel = ctx.channel;

        ctx.channel.attach(playerData);

        field.players.add(playerData);
        field.addGamePiece(playerData.player);
        ctx.channel.sendTCP(new GameInitPacket(playerData.player.getGameId()).setResponsePacket(ctx.packet), null).perform();

    }

    public static void handlePlayerMovePacket(final PacketHandlerContext<PlayerMovePacket> ctx) {
        PlayerData playerData = (PlayerData) ctx.channel.getAttachment();
        Player player = playerData.player;

        if (player==null || player.lastTickUpdate > ctx.packet.tick) return;

        player.lastTickUpdate = ctx.packet.tick;
        GameSettings.runnablePoster.postRunnable(() -> {
            if (player.lastTickUpdate > ctx.packet.tick) return;
            player.bounds.setPosition(ctx.packet.newX, ctx.packet.newY);
            player.update();

            ServerChunk chunk = (ServerChunk) player.chunk;
            // data packets do not work right now

            /*
            DataPacket<PlayerMovePacket> dataPacket = DataPacket.of(
                    ServerLauncher.bufferFactory.buffer(20),
                    new PlayerMovePacket(player.bounds.x, player.bounds.y, player.getGameId(), player.lastTickUpdate)
            );
             */

            Packet packet =  new PlayerMovePacket(player.bounds.x, player.bounds.y, player.getGameId(), player.lastTickUpdate);


            for (PlayerData data : chunk.trackers) {
                if (data == playerData) continue;
                if(data.channel.supportsUDP()) data.channel.sendUDP(packet, null).perform();
                else data.channel.sendTCP(packet, null).perform();
            }
            for (PlayerData data : chunk.softTrackers) {
                if (data == playerData) continue;
                if (data.channel.supportsUDP()) data.channel.sendUDP(packet, null).perform();
                else data.channel.sendTCP(packet, null).perform();
            }
        });

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

    public static void handleShootPacket(PacketHandlerContext<ShootPacket> ctx) {
        PlayerData playerData = (PlayerData) ctx.channel.getAttachment();

        if(playerData.hasShot) return;
        playerData.hasShot = true;

        Player player = playerData.player;

        ServerField field = ServerLauncher.field;
        Fireball fireball = new Fireball(player.getCenterX(), player.getCenterY());
        fireball.maxTime(2F);
        fireball.setSpeed(30F);
        fireball.setAngle(ctx.packet.angle);
        fireball.setRadius(0.05F);
        fireball.setOwnerId(player.getGameId());

        field.addGamePiece(fireball);
    }


}
