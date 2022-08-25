package com.hirshi001.game.shared.tiles;

import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.registry.ID;

public class Tile implements ID {

    private int id;


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
