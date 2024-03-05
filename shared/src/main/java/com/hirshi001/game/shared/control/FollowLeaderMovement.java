package com.hirshi001.game.shared.control;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.IntPoint;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.SearchNode;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.PathFinder;
import com.hirshi001.game.shared.util.Point;

import java.util.LinkedList;

public class FollowLeaderMovement extends Movement {

    public int leaderId;
    float time = 0F;

    LinkedList<IntPoint> path;
    public int targetX, targetY;

    public FollowLeaderMovement() {
        super();
    }

    public FollowLeaderMovement(int leaderId) {
        super();
        this.leaderId = leaderId;
    }

    public void findPath(Troop troop, Pool<SearchNode> pool) {
        Troop leader = (Troop) troop.field.getGamePiece(leaderId);
        if (leader == null) {
            return;
        }

        int lx = (int) Math.floor(leader.getX());
        int ly = (int) Math.floor(leader.getY());

        Movement mov = leader.getMovement();
        if (mov instanceof MoveTroopMovement) {
            MoveTroopMovement moveTroopMovement = (MoveTroopMovement) mov;
            if (moveTroopMovement.path != null) {
                if (moveTroopMovement.path.size() > 5) {
                    IntPoint point = moveTroopMovement.path.get(5);
                    lx = point.x;
                    ly = point.y;
                } else if (moveTroopMovement.path.size()>0) {
                    IntPoint point = moveTroopMovement.path.getLast();
                    lx = point.x;
                    ly = point.y;
                }
            }
        }

        Point p = new Point();
        HashedPoint temp = new HashedPoint();

        for (int i = 0; i < 5; i++) {
            targetX = lx + MathUtils.random(-3, 3);
            targetY = ly + MathUtils.random(-3, 3);
            p.set(targetX, targetY);
            if (troop.field.isWalkable(p, temp)) break;
        }

        if (!troop.field.isWalkable(p, temp)) {
            targetX = lx;
            targetY = ly;
        }

        path = PathFinder.findPathList(troop.field, (int) Math.floor(troop.getX()), (int) Math.floor(troop.getY()), targetX, targetY, pool);

    }

    @Override
    public boolean applyMovement(Troop troop, float delta) {

        if (troop.field.isServer()) {
            time += delta;
            if (time > 1F) {
                FollowLeaderMovement followLeaderMovement = new FollowLeaderMovement(leaderId);
                followLeaderMovement.findPath(troop, null);
                troop.setMovement(followLeaderMovement);
            }
        }

        if (path == null) {
            path = troop.field.findPathList((int) Math.floor(troop.getX()), (int) Math.floor(troop.getY()), targetX, targetY);
        }
        MoveTroopMovement.applyMovement(troop, path, delta * 1.75F);
        return false;
    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {
    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {
        path = null;
    }
}
