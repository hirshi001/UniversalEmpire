package com.hirshi001.game.shared.entities;

import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GamePiece;

import java.util.ArrayList;

public class Knight extends LivingEntity {

    static final CollisionFilter COLLISION_FILTER = (item, other) -> {
        if (other instanceof Entity) {
            Entity entity = (Entity) other;
            if (entity.collides()) {
                return Response.slide;
            }
        }
        return null;
    };


    static ArrayList<Item> queryRect = new ArrayList<>();

    public Knight() {
        super();
        setHealth(200F);
    }

    @Override
    public void setField(Field field) {
        super.setField(field);
        this.bounds.setSize(1F, 1F);
    }

    float time = 5F;

    @Override
    public void tick(float delta) {
        if (field.isServer()) {
            // check health
            if (getHealth() <= 0) {
                alive = false;
                field.removeGamePiece(this);
                return;
            }

            time += delta;
            if (time > 5F) {
                time = 0F;

                field.queryRect(getCenterX() - 10F, getCenterY() - 10F, 20F, 20F, new CollisionFilter() {
                    @Override
                    public Response filter(Item item, Item item1) {
                        if (item instanceof Player) {
                            return Response.touch;
                        }
                        return null;
                    }
                }, queryRect);

                float minDist = Float.MAX_VALUE;
                Player closest = null;
                for (Item item : queryRect) {
                    Player player = (Player) item;
                    float dx = player.getCenterX() - getCenterX();
                    float dy = player.getCenterY() - getCenterY();
                    float dist = dx * dx + dy * dy;
                    if (dist < minDist) {
                        minDist = dist;
                        closest = player;
                    }
                }

                if (closest != null) getProperties().put("target", closest.getGameId());
            }
        }

        Integer targetID = getProperties().get("target");
        if (targetID != null) {
            GamePiece target = field.getGamePiece(targetID);
            if (target != null) {
                float speed = 2F;
                float dx = (target.bounds.x - bounds.x) * speed * delta;
                float dy = (target.bounds.y - bounds.y) * speed * delta;

                Response.Result result = field.move(this, bounds.x + dx, bounds.y + dy, getCollisionFilter());

                bounds.x = result.goalX;
                bounds.y = result.goalY;
            }
        }
    }

    @Override
    public CollisionFilter getCollisionFilter() {
        return COLLISION_FILTER;
    }
}
