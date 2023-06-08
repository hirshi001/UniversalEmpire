package com.hirshi001.game.shared.control;

import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.LivingEntity;
import com.hirshi001.game.shared.entities.troop.Troop;

public class AttackTroopMovement extends Movement {

    public int targetId;

    public AttackTroopMovement(){
        super();

    }

    public AttackTroopMovement(int targetId){
        super();
        this.targetId = targetId;
    }

    @Override
    public boolean applyMovement(Troop troop, float delta) {
        GamePiece gamePiece = troop.field.getGamePiece(targetId);
        if(!(gamePiece instanceof LivingEntity)) return troop.field.isServer();
        LivingEntity target = (LivingEntity) gamePiece;
//        if(MoveTroopMovement.applyMovement(troop, delta, target.bounds.x, target.bounds.y, troop.getAttackRadius())){
//            troop.attack(target);
//            return true;
//        }
        return false;
    }

}
