package com.hirshi001.game.shared.entities;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;

import java.util.ArrayList;

public abstract class Entity extends GamePiece {

    public Entity() {
        this(0, 0);
    }

    public Entity(float x, float y) {
        super();
        this.setPosition(x, y);
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
    }

}
