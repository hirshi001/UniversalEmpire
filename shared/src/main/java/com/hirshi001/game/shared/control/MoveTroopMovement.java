package com.hirshi001.game.shared.control;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.IntPoint;
import com.dongbat.jbump.Response;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.SearchNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MoveTroopMovement extends Movement {

    static Vector2 temp = new Vector2();

    public float x, y, radius2;
    public LinkedList<IntPoint> path;

    public MoveTroopMovement() {
        super(null);
    }

    public MoveTroopMovement(Troop troop, float x, float y, float radius) {
        super(troop);
        this.x = x;
        this.y = y;
        this.radius2 = radius * radius;

        Field field = troop.field;
        path = field.findPathList((int)Math.floor(troop.getCenterX()), (int)Math.floor(troop.getCenterY()), (int)Math.floor(x), (int)Math.floor(y));


     }



    @Override
    public boolean applyMovement(Troop troop, float delta) {
        return applyMovement(troop, path, delta, x, y, radius2);
    }

    public static boolean applyMovement(Troop troop, LinkedList<IntPoint> path, float delta, float goalX, float goalY, float radius2) {
        if(path==null) return true;

        IntPoint next = path.peek();
        if(next==null) return true;


        temp.set(next.x + 0.5F, next.y + 0.5F).sub(troop.getCenterX(), troop.getCenterY()).setLength(troop.getSpeed() * delta).add(troop.bounds.x, troop.bounds.y);

        // troop.move(temp.x, temp.y, troop.getCollisionFilter(), true);

        Response.Result result = troop.field.move(troop, temp.x, temp.y, troop.getCollisionFilter());
        troop.bounds.x = result.goalX;
        troop.bounds.y = result.goalY;

        temp.set(next.x + 0.5F, next.y + 0.5F).sub(troop.getCenterX(), troop.getCenterY());

        if(temp.isZero(0.01F)){
            path.poll();
        }

        return path.isEmpty();
    }

}
