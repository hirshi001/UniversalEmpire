package com.hirshi001.game.shared.entities;

public class Stone extends Resource {

    public Stone(){
        super();
        bounds.setSize(0.8F, 0.8F);
    }
    @Override
    public void harvest() {
        if(field.isServer()){
            field.removeGamePiece(this);
        }
    }
}
