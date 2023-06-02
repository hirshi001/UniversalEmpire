package com.hirshi001.game.shared.control;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.buffer.util.ByteBufUtil;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.networking.packet.ByteBufSerializable;

public class TroopGroup implements ByteBufSerializable {

    public Array<Troop> troops = new Array<>(), dirtyTroops = new Array<>();

    public Field field;
    public String name;
    public int playerControllerId;

    public TroopGroup(Field field, String name, int playerControllerId) {
        this.field = field;
        this.name = name;
        this.playerControllerId = playerControllerId;
    }

    public void addTroop(Troop troop) {
        if(troops.contains(troop, false)) return;
        troops.add(troop);
        dirtyTroops.add(troop);
        troop.setGroup(this);
    }

    public Array<Troop> getDirtyTroops() {
        return dirtyTroops;
    }

    public void removeTroop(Troop troop) {
        troops.removeValue(troop, false);
    }

    public void moveTroops(float destX, float destY, float radius) {
        for (Troop troop : troops) {
            float x, y;
            int i=0;
            do {
                x = destX + (MathUtils.random() * radius * 2 - radius);
                y = destY + (MathUtils.random() * radius * 2 - radius);
                i++;
                if(i>5) break;
            }
            while(field.getTile((int)Math.floor(x), (int)Math.floor(y)).isSolid);

            troop.setMovement(new MoveTroopMovement(troop, x, y, 1F));
        }
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        ByteBufUtil.writeStringToBuf(name, buffer);
        buffer.writeInt(playerControllerId);
        buffer.writeInt(troops.size);
        for (Troop troop : troops) {
            buffer.writeInt(troop.getGameId());
        }
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        name = ByteBufUtil.readStringFromBuf(buffer);
        playerControllerId = buffer.readInt();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            int id = buffer.readInt();
            Troop troop = (Troop) field.getGamePiece(id);
            if (troop != null) troops.add(troop);
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
