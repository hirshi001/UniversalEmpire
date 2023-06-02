package com.hirshi001.game.shared.entities.troop;

import com.hirshi001.game.shared.entities.LivingEntity;
import com.hirshi001.game.shared.game.Field;

public class Knight extends Troop {

    public boolean attacking = false;
    public float attackTime = 0F;
    public LivingEntity attackTarget;

    public Knight() {
    }


    @Override
    public void setField(Field field) {
        super.setField(field);
        setHealth(200);
        setSpeed(2F);
        setAttackRadius(2F);
        setAttackCooldown(2F);
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);

        if (attacking) {
            attackTime += delta;
            if (attackTime >= 0.5F) {
                attacking = false;
                attackTime = 0F;
                if(field.isServer()) attackTarget.damage(10F);
            }
        }
    }

    @Override
    protected void performAttack(LivingEntity target) {
        this.attackTarget = target;
        attacking = true;
        attackTime = 0F;
    }
}
