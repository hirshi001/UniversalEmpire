package com.hirshi001.game.shared.control;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.networking.packet.ByteBufSerializable;

public class TroopGroup implements ByteBufSerializable {

    public Array<Integer> troops = new Array<>();
    public int leaderId;

    public Field field;
    public String name;
    public int playerControllerId;

    public TroopGroup(Field field, String name, int playerControllerId) {
        this.field = field;
        this.name = name;
        this.playerControllerId = playerControllerId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void addTroop(Troop troop) {
        addTroop(troop.getGameId());
    }

    public void addTroop(int troopId) {
        if (troops.contains(troopId, false)) return;

        troops.add(troopId);
        GamePiece piece = field.getGamePiece(troopId);
        if (piece instanceof Troop){
            Troop troop = (Troop) piece;
            TroopGroup oldGroup = troop.getGroup();
            if (oldGroup != null) {
                oldGroup.removeTroop(troopId);
            }
            troop.setGroup(this);
        }
    }

    public void addTroops(Array<Integer> troopIds) {
        for(int i=0;i<troopIds.size;i++){
            addTroop(troopIds.get(i));
        }
    }


    public void removeTroop(Troop troop) {
        removeTroop(troop.getGameId());
    }

    public void removeTroops(Array<Integer> troopIds) {
        for(int i=0;i<troopIds.size;i++){
            removeTroop(troopIds.get(i));
        }
    }

    public void removeTroop(int troopId) {
        troops.removeValue(troopId, false);
        GamePiece piece = field.getGamePiece(troopId);
        if (piece instanceof Troop){
            Troop troop = (Troop) piece;
            troop.setGroup(null);
        }

    }


    @Override
    public void writeBytes(ByteBuffer buffer) {
        ByteBufUtil.writeStringToBuf(name, buffer);
        buffer.writeInt(playerControllerId);
        buffer.writeInt(troops.size);
        for(int i=0;i<troops.size;i++){
            buffer.writeInt(troops.get(i));
        }
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        name = ByteBufUtil.readStringFromBuf(buffer);
        playerControllerId = buffer.readInt();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            int troopId = buffer.readInt();
            troops.add(troopId);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TroopGroup) {
            TroopGroup group = (TroopGroup) obj;
            return group.name.equals(name) && group.playerControllerId == playerControllerId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + playerControllerId;
    }

}
