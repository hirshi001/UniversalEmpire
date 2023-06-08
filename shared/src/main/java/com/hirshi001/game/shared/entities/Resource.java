package com.hirshi001.game.shared.entities;

public abstract class Resource extends  GamePiece{

    public abstract void harvest();

    @Override
    public boolean isStatic() {
        return true;
    }
}
