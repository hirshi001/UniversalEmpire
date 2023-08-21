package com.hirshi001.game.shared.control;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.*;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.Resource;
import com.hirshi001.game.shared.entities.troop.Troop;

import java.util.ArrayList;
import java.util.LinkedList;

public class CollectResourceMovement extends Movement{

    private static ArrayList<Item> items = new ArrayList<>();
    private static Vector2 temp = new Vector2();

    private static CollisionFilter resourceFilter = (item, other) -> {
        if(item instanceof Resource){
            return Response.cross;
        }
        return null;
    };


    float dt = 0;

    LinkedList<IntPoint> path;
    Resource targetResource;

    public CollectResourceMovement(){
        super();
    }


    @Override
    public boolean applyMovement(Troop troop, float delta) {

        /*
        dt -= delta;
        if(dt<=0){
            dt = 2F;
            float searchDistance = 40F;
            troop.field.queryRect(troop.getCenterX()-searchDistance, troop.getCenterY()-searchDistance, 2*searchDistance, 2*searchDistance, resourceFilter, items);
            // get closest resource
            float minDist2 = Float.MAX_VALUE;
            Resource closestResource = null;
            for(Item item : items){
                Resource resource = (Resource)item;
                temp.set(resource.getCenterX(), resource.getCenterY());
                float dist2 = temp.dst2(troop.getCenterX(), troop.getCenterY());
                if(dist2<minDist2){
                    minDist2 = dist2;
                    closestResource = resource;
                }
            }

            boolean hasOldPath = path!=null;
            if(closestResource!=null){
                path = troop.field.findPathList((int)Math.floor(troop.getCenterX()), (int)Math.floor(troop.getCenterY()),
                        (int)Math.floor(closestResource.getCenterX()), (int)Math.floor(closestResource.getCenterY()));
            }else{
                path = null;
            }
            if(path!=null || hasOldPath){
                troop.setMovement(this);
            }
        }

        if(path!=null){
            MoveTroopMovement.applyMovement(troop, path, delta);

        }

         */


        return false;
    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {

    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {

    }
}
