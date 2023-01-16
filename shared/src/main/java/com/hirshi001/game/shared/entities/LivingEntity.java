package com.hirshi001.game.shared.entities;

import com.hirshi001.game.shared.game.Field;

public class LivingEntity extends Entity{

    public LivingEntity() {
        super();
        setHealth(0);
    }

    @Override
    public boolean isProjectile() {
        return false;
    }

    @Override
    public boolean isLivingEntity() {
        return true;
    }

    @Override
    public boolean collides() {
        return true;
    }

    public void setHealth(float health) {
        getProperties().put("health", health);
    }

    public float getHealth() {
        return getProperties().get("health", 0F);
    }

    public void damage(float damage){
        setHealth(getHealth()-damage);
    }
}
