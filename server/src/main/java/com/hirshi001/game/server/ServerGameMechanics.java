package com.hirshi001.game.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Queue;
import com.hirshi001.game.shared.control.FollowLeaderMovement;
import com.hirshi001.game.shared.control.MoveTroopMovement;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.GameMechanics;
import com.hirshi001.game.shared.game.PlayerData;
import com.hirshi001.game.shared.game.SearchNode;
import com.hirshi001.game.shared.packets.TroopGroupPacket;

public class ServerGameMechanics extends GameMechanics {


    private static final ThreadLocal<Pool<SearchNode>> threadLocal = ThreadLocal.withInitial(() -> new Pool<SearchNode>() {
        @Override
        protected SearchNode newObject() {
            return new SearchNode();
        }
    });

    public Queue<Runnable> tasks = new Queue<>();

    public ServerField getField() {
        return (ServerField) field;
    }


    @Override
    public void createTroopGroup(int playerId, String name, Array<Integer> troopIds, int leaderId) {
        PlayerData playerData = getField().players.get(playerId);
        if (playerData != null) {
            TroopGroup troopGroup = new TroopGroup(getField(), name, playerId);
            troopGroup.leaderId = leaderId;

            int leaderIndex = troopIds.indexOf(leaderId, false);
            if (leaderIndex < 0) {
                troopIds.insert(0, leaderId);
            } else {
                troopIds.swap(0, leaderIndex);
            }

            troopGroup.addTroops(troopIds);
            playerData.troopGroups.put(name, troopGroup);
            playerData.channel.sendTCP(new TroopGroupPacket(TroopGroupPacket.OperationType.CREATE, name, troopIds), null).perform();
        }
    }

    @Override
    public void deleteTroopGroup(int playerId, String name) {
        PlayerData playerData = getField().players.get(playerId);
        if (playerData != null) {
            TroopGroup troopGroup = playerData.troopGroups.remove(name);
            if (troopGroup != null) {
                for (int i = 0; i < troopGroup.troops.size; i++) {
                    GamePiece piece = getField().getGamePiece(troopGroup.troops.get(i));
                    if (piece instanceof Troop troop) {
                        troop.setGroup(null);
                    }
                }
            }
            playerData.channel.sendTCP(new TroopGroupPacket(TroopGroupPacket.OperationType.DELETE, name, null), null).perform();
        }
    }

    @Override
    public TroopGroup getTroopGroup(int playerId, String name) {
        PlayerData playerData = getField().players.get(playerId);
        if (playerData != null) {
            return playerData.troopGroups.get(name);
        }
        return null;
    }

    @Override
    public void addTroopsToGroup(int playerId, String name, Array<Integer> troopIds) {
        PlayerData playerData = getField().players.get(playerId);
        if (playerData != null) {

            for (int i = 0; i < troopIds.size; i++) {
                GamePiece piece = getField().getGamePiece(troopIds.get(i));
                if (piece instanceof Troop troop) {
                    TroopGroup oldGroup = troop.getGroup();
                    if (oldGroup != null) {
                        oldGroup.removeTroop(troop);
                    }
                }
            }

            TroopGroup troopGroup = playerData.troopGroups.get(name);
            if (troopGroup != null) {
                troopGroup.addTroops(troopIds);
            }

            playerData.channel.sendTCP(new TroopGroupPacket(TroopGroupPacket.OperationType.ADD, name, troopIds), null).perform();
        }
    }

    @Override
    public void removeTroopsFromGroup(int playerId, String name, Array<Integer> troopIds) {
        PlayerData playerData = getField().players.get(playerId);
        if (playerData != null) {
            TroopGroup troopGroup = playerData.troopGroups.get(name);
            if (troopGroup != null) {
                troopGroup.removeTroops(troopIds);
            }
            playerData.channel.sendTCP(new TroopGroupPacket(TroopGroupPacket.OperationType.REMOVE, name, troopIds), null).perform();
        }
    }

    @Override
    public void moveTroopGroup(int playerId, String name, float destX, float destY) {
        PlayerData playerData = getField().players.get(playerId);
        if (playerData == null)
            return;

        TroopGroup troopGroup = playerData.troopGroups.get(name);
        if (troopGroup == null) return;

        Troop troop = (Troop) field.getGamePiece(troopGroup.leaderId);
        if (troop != null) {
            Troop finalTroop = troop;
            getField().getExec().runDeferred(() -> {
                final MoveTroopMovement movement = new MoveTroopMovement(destX, destY, 1F);
                movement.findPath(finalTroop, threadLocal.get());
                finalTroop.setMovement(movement);

            });
        }


        for (int i = 0; i < troopGroup.troops.size; i++) {
            Integer troopId = troopGroup.troops.get(i);
            float x, y;

            // amount of times to try to find a valid position
            float radius = 2F;
            int checkIterations = 5;

            do {
                x = destX + (MathUtils.random() * radius * 2 - radius);
                y = destY + (MathUtils.random() * radius * 2 - radius);
                checkIterations++;
            } while (checkIterations > 0 && !getField().isWalkable((int) Math.floor(x), (int) Math.floor(y)));

            troop = (Troop) field.getGamePiece(troopId);
            if (troop != null) {
                Troop finalTroop1 = troop;
                getField().getExec().runDeferred( ()-> {
                    final FollowLeaderMovement movement = new FollowLeaderMovement(troopGroup.getLeaderId());
                    finalTroop1.setMovement(movement);
                });
            }
        }
    }
}
