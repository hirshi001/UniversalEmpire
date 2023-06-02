package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.control.AttackTroopMovement;

public class AttackTroopMovementSerializer implements ByteBufSerializer<AttackTroopMovement> {

    @Override
    public void serialize(AttackTroopMovement object, ByteBuffer buffer) {
        buffer.writeInt(object.targetId);
    }

    @Override
    public AttackTroopMovement deserialize(ByteBuffer buffer) {
        AttackTroopMovement object = new AttackTroopMovement(null);
        object.targetId = buffer.readInt();
        return object;
    }
}

