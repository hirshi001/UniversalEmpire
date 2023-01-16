package com.hirshi001.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.hirshi001.game.shared.entities.Player;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.util.props.Properties;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;

public class PacketHandlers {

    public static void handleChunkPacket(PacketHandlerContext<ChunkPacket> ctx) {

        ClientField field = GameApp.Game().field;
        if (field != null) {
            Gdx.app.postRunnable(() -> {
                Chunk chunk = ctx.packet.chunk;
                field.addChunk(chunk);
            });
        }
    }

    public static void handleGamePieceSpawnPacket(PacketHandlerContext<GamePieceSpawnPacket> ctx) {
        ClientField field = GameApp.Game().field;
        if (field != null) {
            Gdx.app.postRunnable(() -> {
                field.addGamePiece(ctx.packet.gamePiece, ctx.packet.gamePiece.getGameId());
            });
        }
    }

    public static void handleGameInitPacket(PacketHandlerContext<GameInitPacket> ctx) {
        if (GameApp.Game().field == null) return;
        GameApp.Game().field.playerId = ctx.packet.playerId;
    }

    public static void handleGamePieceDespawnPacket(PacketHandlerContext<GamePieceDespawnPacket> ctx) {
        ClientField field = GameApp.Game().field;
        if (field != null) {
            Gdx.app.postRunnable(() -> {
                field.removeGamePiece(ctx.packet.gamePieceID);
            });
        }
    }

    public static void handleSyncPacket(PacketHandlerContext<SyncPacket> ctx) {
        ClientField field = GameApp.Game().field;
        if (field != null) {
            Gdx.app.postRunnable(() -> {
                long tick = ctx.packet.time;
                GamePiece gamePiece = field.getGamePiece(ctx.packet.id);
                if (gamePiece != null) {
                    if (gamePiece.lastTickUpdate > tick) return;
                    gamePiece.lastTickUpdate = tick;
                    try {
                        gamePiece.readSyncBytes(ctx.packet.buffer);
                        gamePiece.update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public static void handlePropertyPacket(PacketHandlerContext<PropertyPacket> ctx) {
        ClientField field = GameApp.Game().field;
        if (field == null) return;
        Gdx.app.postRunnable(() -> {
            PropertyPacket packet = ctx.packet;
            GamePiece gamePiece = field.getGamePiece(packet.gamePieceId);
            if (gamePiece != null) {
                Properties properties = gamePiece.getProperties();
                String name = properties.getKeyName(packet.propertyId);
                if (name != null) properties.put(name, packet.value);
                else {
                    ctx.channel.sendTCP(new RequestPropertyNamePacket(packet.gamePieceId, packet.propertyId), null).perform();
                }
            }
        });

    }

    public static void handlePropertyNamePacket(PacketHandlerContext<PropertyNamePacket> ctx) {
        ClientField field = GameApp.Game().field;
        if (field == null) return;
        PropertyNamePacket packet = ctx.packet;
        GamePiece piece = field.getGamePiece(packet.gamePieceId);
        if (piece == null) return;
        GameSettings.runnablePoster.postRunnable(() -> piece.getProperties().put(packet.propertyName, packet.propertyId, packet.value));
    }

    public static void handlePlayerMovePacket(PacketHandlerContext<PlayerMovePacket> ctx) {
        System.out.println("Received player move packet");
        GameSettings.runnablePoster.postRunnable(() -> {
            ClientField field = GameApp.Game().field;
            System.out.println("A");
            if (field == null) return;

            GamePiece gamePiece = field.getGamePiece(ctx.packet.id);
            System.out.println("B");
            if (!(gamePiece instanceof Player)) return;
            Player player = (Player) gamePiece;
            System.out.println("C");
            if (player.lastTickUpdate > ctx.packet.tick) return;


            Vector2 dst  = new Vector2();
            if(ctx.packet.forceMove || player!=field.getPlayer() || player.bounds.getPosition(dst).dst2(ctx.packet.newX, ctx.packet.newY) > 4F){
                player.bounds.setPosition(ctx.packet.newX, ctx.packet.newY);
                System.out.println("D");
                player.update();
            }

        });
    }

}
