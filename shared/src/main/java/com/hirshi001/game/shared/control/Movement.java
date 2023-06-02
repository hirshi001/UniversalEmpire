package com.hirshi001.game.shared.control;

import com.hirshi001.game.shared.entities.troop.Troop;

public abstract class Movement {

    public Troop troop;

    public Movement(Troop troop){
        this.troop = troop;
    }

    public abstract boolean applyMovement(Troop troop, float delta);

}
