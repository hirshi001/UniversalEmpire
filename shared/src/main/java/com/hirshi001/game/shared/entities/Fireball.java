package com.hirshi001.game.shared.entities;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Collisions;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.settings.GameSettings;

public class Fireball extends Entity {

    protected LinePath.LinePathParam pathParam = new LinePath.LinePathParam();
    private static final CollisionFilter filter = new CollisionFilter() {
        @Override
        public Response filter(Item item, Item other) {
            if(!(other instanceof Player)) return null;
            Player player = (Player) other;

            int id = ((Fireball) item).getProperties().get("ownId", -1);
            if(id==player.getGameId()) return null;
            return Response.touch;
        }
    };

    public Fireball() {
    }

    public Fireball(float x, float y) {
        super(x, y, 0.5F, 0.5F);
    }

    @Override
    public void setField(Field field) {
        super.setField(field);
    }

    float time=0F;

    @Override
    public void tick(float delta) {
        super.tick(delta);
        Number radius = getProperties().get("radius", 0.5F);
        if(bounds.width!=radius.floatValue() || bounds.height!=radius.floatValue()){
            bounds.setSize(radius.floatValue(), radius.floatValue());
            update();
        }


        Number angle = getProperties().get("angle");
        if(angle!=null) {
            Number speed = getProperties().get("speed", 10F);
            float dx = (float) (Math.cos(angle.doubleValue()) * speed.floatValue() * delta);
            float dy = (float) (Math.sin(angle.doubleValue()) * speed.floatValue() * delta);

            Response.Result result = field.move(this, bounds.x + dx, bounds.y + dy, filter);
            bounds.x = result.goalX;
            bounds.y = result.goalY;

            Collisions collisions = result.projectedCollisions;
            if(collisions.size()>0 && field.isServer()) {
                System.out.println("Removed Fireball");
                field.removeGamePiece(this);
                Item item = collisions.get(0).other;
                if(item instanceof Player){
                    Player player = (Player) item;
                    player.getProperties().update("health", (health)->((Number)health).floatValue()-10F);
                }
            }
        }

        if(field.isServer()){
            time+=delta;
            Number maxTime = getProperties().get("maxTime");
            if(maxTime!=null && time>=maxTime.floatValue()){
                field.removeGamePiece(this);
            }
        }

    }

    public void setSpeed(float speed){
        getProperties().put("speed", speed);
    }

    public void setAngle(float angle){
        getProperties().put("angle",  angle);
    }

    public void maxTime(float time){
        getProperties().put("maxTime",  time);
    }

    public void setOwnerId(int id){
        getProperties().put("ownId", id);
    }

    public void setRadius(float radius) {
        getProperties().put("r", radius);
        GameSettings.runnablePoster.postRunnable(()->{
            float r = getProperties().get("r", 1F);
            bounds.setSize(r*2, r*2);
            update();
        });
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {
        super.writeBytes(buffer);
        buffer.writeFloat(pathParam.getDistance());
    }

    @Override
    public void readBytes(ByteBuffer buffer) {
        super.readBytes(buffer);
        pathParam.setDistance(buffer.readFloat());
    }

    @Override
    public void writeSyncBytes(ByteBuffer buffer) {
        buffer.writeFloat(pathParam.getDistance());
    }

    @Override
    public void readSyncBytes(ByteBuffer buffer) {
        pathParam.setDistance(buffer.readFloat());
    }

    @Override
    public boolean shouldLoadChunk() {
        return true;
    }
}
