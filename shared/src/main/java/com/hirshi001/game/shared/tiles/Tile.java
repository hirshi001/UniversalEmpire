package com.hirshi001.game.shared.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.registry.ID;
import com.hirshi001.game.shared.util.Range;

import java.util.ArrayList;
import java.util.List;

public class Tile implements ID {

    private int id;

    public boolean isSolid = false;

    public Range temperature, humidity, height, plantGrowth;
    public TextureRegion texture;


    public Tile(){
        temperature = Range.largestRange();
        humidity = Range.largestRange();
        height = Range.largestRange();
        plantGrowth = Range.largestRange();
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
