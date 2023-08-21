package com.hirshi001.game.shared.entities.troop;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Response;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.control.Movement;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.Entity;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.LivingEntity;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.PlayerData;
import com.hirshi001.game.shared.settings.GameSettings;

public abstract class Troop extends LivingEntity {

    float cooldownTime = 0F;
    float time = 0F;
    Vector2 targetPosition = new Vector2();
    public Troop() {
        super();
    }

    @Override
    public void setField(Field field) {
        super.setField(field);
        update();
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        if (field.isServer()) cooldownTime -= delta;

        move(delta);
        checkHealth();
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        super.writeBytes(buffer);
        buffer.writeFloat(time);
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        super.readBytes(buffer);
        time = buffer.readFloat();
    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {
        super.writeSyncBytes(buffer);
        buffer.writeFloat(time);
        buffer.writeBoolean(getMovement()!=null);
        if(getMovement()!=null) {
            getMovement().writeSyncBytes(buffer);
        }
    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {
        super.readSyncBytes(buffer);
        time = buffer.readFloat();
        if(buffer.readBoolean() && getMovement()!=null) {
            getMovement().readSyncBytes(buffer);
        }
    }

    @Override
    public boolean needsSync() {
        return true;
    }

    protected void move(float delta) {
        time += delta;


        Movement movement = getMovement();
        if (movement != null) {
            if (movement.applyMovement(this, delta) && field.isServer()) {
                setMovement(null);
            }
            return;
        }
        if(getGroup() != null) {
            Troop troop = (Troop) field.getGamePiece(getGroup().getLeaderId());
            if(time>5F) {
                time = 0F;
                if(troop!=null) {
                    targetPosition.set(troop.getPosition());
                }
            }


            if(troop!=null) {
                Vector2 deltaPos = targetPosition.cpy().sub(getPosition()).setLength(getSpeed() * delta);
                field.moveGamePieceShort(this, getX() + deltaPos.x, getY() + deltaPos.y);
            }
        }
    }

    protected void checkHealth() {
        if(getHealth()<=0) setHealth(0);
        if (getHealth() == 0 && field.isServer()) {
            alive = false;
            onDeath();
            field.removeGamePiece(this);
        }
    }

    // TODO: Make sure TroopGroup is foolproof
    protected void onDeath() {
        TroopGroup group = getGroup();
        if(group!=null) {
            group.removeTroop(this);
            setGroup(null);
        }
    }

    public void attack(LivingEntity target) {
        if (field.isServer() && cooldownTime <= 0) {
            performAttack(target);
            cooldownTime = getAttackCooldown();
        }
    }



    public void setGroup(TroopGroup group) {
        // For visual purposes only so that not all clients will know the troop group of this troop
        // Should be set by the server only when a packet is received by the client or server
        if(group==null) getProperties().putLocal("group", null);
        else getProperties().putLocal("group", group.name);

    }

    public TroopGroup getGroup() {
        String name = getProperties().get("group");
        if(name==null) return null;
        return field.getGameMechanics().getTroopGroup(getControllerId(), name);
    }

    public String getGroupName() {
        return getProperties().get("group");
    }

    protected abstract void performAttack(LivingEntity target);


    public TroopTier getTroopTier() {
        return getProperties().get("troopTier", TroopTier.WOOD);
    }

    public void setTroopTier(TroopTier troopTier) {
        getProperties().put("troopTier", troopTier);
    }

    public void setControllerId(int controllerId) {
        getProperties().put("contId", controllerId);
    }

    public int getControllerId() {
        return getProperties().get("contId", -1);
    }

    public void setMovement(Movement movement) {
        getProperties().put("mvmnt", movement);
    }

    public Movement getMovement() {
        return getProperties().get("mvmnt", null);
    }

    public void setAttackCooldown(float attackCooldown) {
        getProperties().put("atkCD", attackCooldown);
    }

    public float getAttackCooldown() {
        return getProperties().get("atkCD", 0F);
    }

    public void setAttackRadius(float attackRadius) {
        getProperties().put("atkRad", attackRadius);
    }

    public float getAttackRadius() {
        return getProperties().get("atkRad", 0F);
    }

}
