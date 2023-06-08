package com.hirshi001.game.shared.control;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

public class TroopLocation implements Location<Vector2> {

    Vector2 position;
    float orientation;

    public TroopLocation() {
        this.position = new Vector2();
        this.orientation = 0;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return vector.angleRad();
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return outVector.setAngleRad(angle);
    }

    @Override
    public TroopLocation newLocation() {
        return new TroopLocation();
    }
}
