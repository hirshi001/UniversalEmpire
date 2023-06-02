package com.hirshi001.game.shared.tiles;

import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.registry.ID;

public class Tile implements ID {

    private int id;

    public boolean isSolid = false;


    public Tile(){
    }

    public Tile(boolean isSolid){
        this.isSolid = isSolid;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    public void onGamePieceMove(GamePiece gamePiece) {

    }




}
