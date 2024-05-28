package com.hirshi001.game.shared.control;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.IntPoint;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.SearchNode;
import com.hirshi001.game.shared.util.PathFinder;
import java.util.LinkedList;


public class MoveTroopMovement extends Movement {

    static Vector2 temp = new Vector2();

    public float x, y, radius2;
    public LinkedList<IntPoint> path;

    public MoveTroopMovement() {
        super();
    }

    public MoveTroopMovement(float x, float y, float radius) {
        super();
        this.x = x;
        this.y = y;
        this.radius2 = radius * radius;



        /*
        FormationPattern<Vector2> pattern = new OffensiveCircleFormationPattern<>(1F);

        TroopLocation anchor = new TroopLocation();
        anchor.getPosition().set()
        Formation<Vector2> formation = new Formation<>();
         */

    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {
        buffer.writeByte(Movement.MOVE_TROOP_MOVEMENT);
        buffer.writeInt(path.size());
    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {
        if(buffer.readByte()!=Movement.MOVE_TROOP_MOVEMENT) return;

        // trim path to size
        int size = buffer.readInt();
        if(path!=null) {
            while(path.size()>size) {
                path.poll();
            }
        }
    }

    public void findPath(Troop troop, Pool<SearchNode> pool) {
        path = PathFinder.findPathList(troop.field, (int) Math.floor(troop.getX()), (int) Math.floor(troop.getY()), (int) Math.floor(x), (int) Math.floor(y), pool);
        // path = troop.field.findPathList((int) Math.floor(troop.getX()), (int) Math.floor(troop.getY()), (int) Math.floor(x), (int) Math.floor(y));
    }

    @Override
    public boolean applyMovement(Troop troop, float delta) {
        if (path == null) {
            findPath(troop, null);
        }
        return applyMovement(troop, path, delta);
    }

    public static boolean applyMovement(Troop troop, LinkedList<IntPoint> path, float delta) {
        if (path == null) return true;

        IntPoint next = path.peek();
        if (next == null) return true;
        Vector2 temp = new Vector2();

        temp.set(next.x + 0.5F, next.y + 0.5F).sub(troop.getPosition()).setLength(troop.getSpeed() * delta).add(troop.getPosition());

        // troop.move(temp.x, temp.y, troop.getCollisionFilter(), true);

        troop.field.moveGamePieceShort(troop, temp.x, temp.y);

        temp.set(next.x + 0.5F, next.y + 0.5F).sub(troop.getX(), troop.getY());

        if (temp.isZero(0.01F)) {
            path.poll();
        }

        return path.isEmpty();
    }

}
