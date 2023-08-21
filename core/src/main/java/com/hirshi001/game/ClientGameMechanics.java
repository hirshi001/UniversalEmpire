package com.hirshi001.game;

import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GameMechanics;
import com.hirshi001.game.shared.packets.TroopGroupPacket;
import com.hirshi001.networking.packethandlercontext.PacketType;

public class ClientGameMechanics extends GameMechanics {

    @Override
    public ClientField getField() {
        return (ClientField) field;
    }

    /**
     * Sends a request to the server to create a new troop group. The request may not be accepted by the server, but if
     * it is, the server will send a TroopGroupPacket with the newly created troop group.
     *
     * @param playerId the id of the player who is creating the troop group - must be this players id
     * @param name     the name of the troop group
     * @param troopIds the ids of the troops to add to the group
     * @param leaderId the id of the troop to be the leader of the group
     */
    @Override
    public void createTroopGroup(int playerId, String name, Array<Integer> troopIds, int leaderId) {
        if (playerId != getField().getControllerId()) return;
        int leaderIdx = troopIds.indexOf(leaderId, false);

        // make leader the first index in the array
        if (leaderIdx < 0) {
            troopIds.insert(0, leaderId);
        } else {
            troopIds.swap(0, leaderIdx);
        }

        getField().client.getChannel().send(new TroopGroupPacket(TroopGroupPacket.OperationType.CREATE, name, troopIds), null, PacketType.TCP).perform();
    }

    /**
     * Sends a request to the server to delete a troop group. The request may not be accepted by the server, but if it
     * is, the server will send a TroopGroupPacket with the deleted troop group.
     *
     * @param playerId the id of the player who is deleting the troop group - must be this players id
     * @param name     the name of the troop group
     */
    @Override
    public void deleteTroopGroup(int playerId, String name) {
        if (playerId != getField().getControllerId()) return;
        getField().client.getChannel().send(new TroopGroupPacket(TroopGroupPacket.OperationType.DELETE, name, null), null, PacketType.TCP).perform();
    }

    /**
     * Sends a request to the server to add troops to a troop group. The request may not be accepted by the server, but
     * if it is, the server will send a TroopGroupPacket with the updated troop group.
     *
     * @param playerId the id of the player who is adding the troops - must be this players id
     * @param name     the name of the troop group
     * @param troopIds the ids of the troops to add to the group
     */
    @Override
    public void addTroopsToGroup(int playerId, String name, Array<Integer> troopIds) {
        if (playerId != getField().getControllerId()) return;
        getField().client.getChannel().send(new TroopGroupPacket(TroopGroupPacket.OperationType.ADD, name, troopIds), null, PacketType.TCP).perform();
    }

    /**
     * Sends a request to the server to remove troops from a troop group. The request may not be accepted by the server,
     * but if it is, the server will send a TroopGroupPacket with the updated troop group.
     *
     * @param playerId the id of the player who is removing the troops - must be this players id
     * @param name     the name of the troop group
     * @param troopIds the ids of the troops to remove from the group
     */
    @Override
    public void removeTroopsFromGroup(int playerId, String name, Array<Integer> troopIds) {
        if (playerId != getField().getControllerId()) return;
        getField().client.getChannel().send(new TroopGroupPacket(TroopGroupPacket.OperationType.REMOVE, name, troopIds), null, PacketType.TCP).perform();
    }

    @Override
    public void moveTroopGroup(int playerId, String name, float x, float y) {
        // TODO: implement
    }


    @Override
    public TroopGroup getTroopGroup(int playerId, String name) {
        if (playerId == getField().getControllerId()) {
            return getField().playerData.troopGroups.get(name);
        }
        return null;
    }
}
