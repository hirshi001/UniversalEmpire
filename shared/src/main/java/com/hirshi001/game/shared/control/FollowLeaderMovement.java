package com.hirshi001.game.shared.control;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.IntPoint;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.troop.Troop;

import java.util.LinkedList;

public class FollowLeaderMovement extends Movement {

    public int leaderId;
    float time = 5F;

    LinkedList<IntPoint> path;
    int targetX, targetY;
    boolean remakePath = false;

    public FollowLeaderMovement() {
        super();
    }

    public FollowLeaderMovement(int leaderId) {
        super();
        this.leaderId = leaderId;
    }

    @Override
    public boolean applyMovement(Troop troop, float delta) {
        Troop leader = (Troop) troop.field.getGamePiece(leaderId);
        if (leader == null) {
            return true;
        }
        if (troop.field.isServer()) {
            time += delta;
            if (time > 2) {
                int lx = (int) Math.floor(leader.getX());
                int ly = (int) Math.floor(leader.getY());

                for (int i = 0; i < 5; i++) {
                    targetX = lx + MathUtils.random(-3, 3);
                    targetY = ly + MathUtils.random(-3, 3);
                    if (troop.field.isWalkable(targetX, targetY)) break;
                }

                if (!troop.field.isWalkable(targetX, targetY)) {
                    targetX = lx;
                    targetY = ly;
                }
                time = 0;
                remakePath = true;
            }
        }
        if (remakePath) {
            path = troop.field.findPathList((int) Math.floor(troop.getX()), (int) Math.floor(troop.getY()), targetX, targetY);
            remakePath = false;
        }

        MoveTroopMovement.applyMovement(troop, path, delta * 1.25F);
        return false;
    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {
        buffer.writeInt(Movement.FOLLOW_LEADER_MOVEMENT);
        buffer.writeInt(targetX);
        buffer.writeInt(targetY);
    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {
        if (buffer.readInt() != Movement.FOLLOW_LEADER_MOVEMENT) return;
        targetX = buffer.readInt();
        targetY = buffer.readInt();
        remakePath = true;
    }
}
