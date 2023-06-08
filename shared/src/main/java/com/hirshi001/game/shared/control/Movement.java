package com.hirshi001.game.shared.control;

import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.Field;

public abstract class Movement {

    public Movement(){
    }

    public abstract boolean applyMovement(Troop troop, float delta);

}
