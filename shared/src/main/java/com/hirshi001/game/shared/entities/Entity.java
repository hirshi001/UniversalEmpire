package com.hirshi001.game.shared.entities;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;

import java.util.ArrayList;

public abstract class Entity extends GamePiece {

    static Vector2 temp = new Vector2();
    static ArrayList<Item> tempArray = new ArrayList<>();
    static CollisionFilter pushFilter = (item, other) -> {
        if (!(item instanceof GamePiece)) return null;
        GamePiece gamePiece = (GamePiece) item;
        if(gamePiece.collides() && !gamePiece.isStatic()) return Response.cross;
        return null;
    };



    public Entity() {
        this(0, 0);
    }

    public Entity(float x, float y) {
        this(x, y, 0, 0);
    }

    public Entity(float x, float y, float width, float height) {
        this.bounds.set(x, y, width, height);
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        /*
        Rules for movement and collision filters:
        If the piece is static, it will not move, and will not be moved by other pieces, so use Response.slide
        IF the piece.collides is false, use Response.cross
        If the piece is not static and collides, also use cross as the response since calling Entity.tick with is will handle pushing.

        To move an entity with pushing, call Entity.move with push = true.
         */
        field.queryRect(bounds.x, bounds.y, bounds.width, bounds.height, pushFilter, tempArray);
        for(Item item : tempArray){
            if(item == this) continue;
            if(!(item instanceof GamePiece)) continue;

            GamePiece piece = (GamePiece) item;
            if(piece.isStatic() || !piece.collides()) continue;

            // push the other piece and this piece back very slightly

            // first push other piece
            // get direction to push the piece
            temp.set(piece.getCenterX(), piece.getCenterY()).sub(getCenterX(), getCenterY());
            // set length
            temp.setLength(0.5F * delta);
            // get destination
            temp.add(piece.bounds.x, piece.bounds.y);

            Response.Result result = field.move(piece, temp.x, temp.y, piece.getCollisionFilter());
            piece.bounds.x = result.goalX;
            piece.bounds.y = result.goalY;

            // now push this piece
            // get direction to push the piece
            temp.set(getCenterX(), getCenterY()).sub(piece.getCenterX(), piece.getCenterY());
            // set length
            temp.setLength(0.5F * delta);
            // get destination
            temp.add(bounds.x, bounds.y);

            result = field.move(this, temp.x, temp.y, getCollisionFilter());
            bounds.x = result.goalX;
            bounds.y = result.goalY;
        }
    }

    @Override
    public boolean isStatic() {
        return false;
    }



}
