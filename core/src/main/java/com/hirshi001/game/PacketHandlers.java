package com.hirshi001.game;

import com.badlogic.gdx.Gdx;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.Fireball;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.game.PlayerData;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.util.props.Properties;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;

public class PacketHandlers {

    public static void handleChunkPacket(PacketHandlerContext<ChunkPacket> ctx) {
        ClientField field = GameApp.field;
        if (field != null) {
            Gdx.app.postRunnable(() -> {
                Chunk chunk = ctx.packet.chunk;
                field.addChunk(chunk);
            });
        }
    }

    public static void handleGamePieceSpawnPacket(PacketHandlerContext<GamePieceSpawnPacket> ctx) {
        ClientField field = GameApp.field;
        if (field != null) {
            Gdx.app.postRunnable(() -> {
                field.addGamePiece(ctx.packet.gamePiece, ctx.packet.gamePiece.getGameId());
            });
        }
    }

    public static void handleGameInitPacket(PacketHandlerContext<GameInitPacket> ctx) {
        if (GameApp.field == null) return;
        GameApp.field.playerData.controllerId = ctx.packet.playerControllerId;
    }

    public static void handleGamePieceDespawnPacket(PacketHandlerContext<GamePieceDespawnPacket> ctx) {
        ClientField field = GameApp.field;
        if (field != null) {
            Gdx.app.postRunnable(() -> field.removeGamePiece(ctx.packet.gamePieceID));
        }
    }

    public static void handleSyncPacket(PacketHandlerContext<SyncPacket> ctx) {
        ClientField field = GameApp.field;
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
        ClientField field = GameApp.field;
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
        ClientField field = GameApp.field;
        if (field == null) return;
        PropertyNamePacket packet = ctx.packet;
        GamePiece piece = field.getGamePiece(packet.gamePieceId);
        if (piece == null) return;
        GameSettings.runnablePoster.postRunnable(() -> piece.getProperties().put(packet.propertyName, packet.propertyId, packet.value));
    }


    public static void handleTroopGroupPacket(PacketHandlerContext<TroopGroupPacket> ctx) {
        ClientField field = GameApp.field;
        TroopGroupPacket packet = ctx.packet;
        PlayerData playerData = field.playerData;

        GameSettings.runnablePoster.postRunnable( ()-> {
            try {
                if (packet.type == TroopGroupPacket.OperationType.CREATE) {
                    TroopGroup troopGroup = new TroopGroup(field, packet.name, playerData.controllerId);
                    troopGroup.setLeaderId(packet.troopIds[0]);
                    System.out.println("Troop leader id: " + troopGroup.getLeaderId());
                    playerData.troopGroups.put(packet.name, troopGroup);
                }

                if (packet.type == TroopGroupPacket.OperationType.ADD || packet.type == TroopGroupPacket.OperationType.CREATE) {
                    TroopGroup troopGroup = playerData.troopGroups.get(packet.name);
                    for (int i = 0; i < packet.troopIds.length; i++) {
                        troopGroup.addTroop(packet.troopIds[i]);
                    }
                }

                if (packet.type == TroopGroupPacket.OperationType.REMOVE) {
                    TroopGroup troopGroup = playerData.troopGroups.get(packet.name);
                    for (int i = 0; i < packet.troopIds.length; i++) {
                        troopGroup.removeTroop(packet.troopIds[i]);
                    }
                }

                if (packet.type == TroopGroupPacket.OperationType.DELETE) {
                    playerData.troopGroups.remove(packet.name);
                }
            }catch (Exception e){e.printStackTrace();}
        });

    }


}
