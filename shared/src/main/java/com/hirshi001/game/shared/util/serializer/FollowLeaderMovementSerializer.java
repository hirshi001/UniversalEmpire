package com.hirshi001.game.shared.util.serializer;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.control.FollowLeaderMovement;

public class FollowLeaderMovementSerializer implements ByteBufSerializer<FollowLeaderMovement>{
    @Override
    public void serialize(FollowLeaderMovement object, ByteBuffer buffer) {
        buffer.writeInt(object.leaderId);
        buffer.writeInt(object.targetX);
        buffer.writeInt(object.targetY);
    }

    @Override
    public FollowLeaderMovement deserialize(ByteBuffer buffer) {
        FollowLeaderMovement object = new FollowLeaderMovement();
        object.leaderId = buffer.readInt();
        object.targetX = buffer.readInt();
        object.targetY = buffer.readInt();
        return object;
    }
}
