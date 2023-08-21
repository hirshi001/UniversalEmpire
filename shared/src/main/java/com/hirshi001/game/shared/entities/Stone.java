package com.hirshi001.game.shared.entities;

public class Stone extends Resource {

    public Stone(){
        super();
    }


    public Stone(int x, int y){
        super(x, y);
    }
    @Override
    public void harvest() {
        if(field.isServer()){
            field.removeGamePiece(this);
        }
    }
}
