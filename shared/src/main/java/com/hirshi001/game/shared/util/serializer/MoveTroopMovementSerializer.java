package com.hirshi001.game.shared.util.serializer;

import com.dongbat.jbump.IntPoint;
import com.hirshi001.betternetworkingutil.ByteBufSerializer;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.control.MoveTroopMovement;
import java.util.LinkedList;

public class MoveTroopMovementSerializer implements ByteBufSerializer<MoveTroopMovement> {


    @Override
    public void serialize(MoveTroopMovement object, ByteBuffer buffer) {
        buffer.writeFloat(object.x);
        buffer.writeFloat(object.y);
        buffer.writeFloat(object.radius2);
        LinkedList<IntPoint> path = object.path;

        buffer.writeInt(path.size());
        for(IntPoint point : path){
            buffer.writeInt(point.x);
            buffer.writeInt(point.y);
        }
    }

    @Override
    public MoveTroopMovement deserialize(ByteBuffer buffer) {
        MoveTroopMovement object = new MoveTroopMovement();
        object.x = buffer.readFloat();
        object.y = buffer.readFloat();
        object.radius2 = buffer.readFloat();

        int size = buffer.readInt();
        LinkedList<IntPoint> path = new LinkedList<>();
        for(int i=0; i<size; i++){
            path.add(new IntPoint(buffer.readInt(), buffer.readInt()));
        }
        object.path = path;
        return object;
    }
}
