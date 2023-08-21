package com.hirshi001.game.shared.util.formation;

import com.badlogic.gdx.ai.fma.FormationPattern;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.networking.packet.ByteBufSerializable;

public class SerializableFormationPattern implements ByteBufSerializable, FormationPattern<Vector2> {

    @Override
    public void setNumberOfSlots(int numberOfSlots) {

    }

    @Override
    public Location<Vector2> calculateSlotLocation(Location<Vector2> outLocation, int slotNumber) {
        return null;
    }

    @Override
    public boolean supportsSlots(int slotCount) {
        return false;
    }

    @Override
    public void writeBytes(ByteBuffer buffer) {

    }

    @Override
    public void readBytes(ByteBuffer buffer) {

    }

}
