package com.hirshi001.game.shared.control;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.Field;

public abstract class Movement {

    private static int id=0;
    public static int MOVE_TROOP_MOVEMENT = id++;
    public static int ATTACK_TROOP_MOVEMENT = id++;
    public static int COLLECT_RESOURCE_MOVEMENT = id++;
    public static int FOLLOW_LEADER_MOVEMENT = id++;

    public Movement(){
    }

    public abstract boolean applyMovement(Troop troop, float delta);

    public abstract void writeSyncBytes(ByteBuffer buffer);

    public abstract void readSyncBytes(ByteBuffer buffer);



}
