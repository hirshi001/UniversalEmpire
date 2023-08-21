package com.hirshi001.game.shared.entities;

import com.hirshi001.game.shared.entities.tilepieces.TileGamePiece;

public abstract class Resource extends TileGamePiece {

    public Resource(){

    }

    public Resource(int x, int y){
        super(x, y, 1, 1);
    }

    public abstract void harvest();

}
