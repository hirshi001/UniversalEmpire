package com.hirshi001.game.shared.entities;

import com.hirshi001.game.shared.game.GamePiece;


public abstract class Entity extends GamePiece {



    public Entity() {
        this(0, 0);
    }

    public Entity(float x, float y) {
        this(x, y, 0, 0);
    }

    public Entity(float x, float y, float width, float height){
        this.bounds.set(x, y, width, height);
    }



    @Override
    public boolean isStatic() {
        return false;
    }

}
