package com.hirshi001.game.render.tilerenderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hirshi001.game.shared.entities.TileGamePiece;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.tiles.Tile;

public class StoneTileRenderer extends DefaultTileRenderer<Tile>{

    public StoneTileRenderer(Tile tile, TextureRegion region) {
        super(tile, region);
    }

    @Override
    public void render(SpriteBatch batch, Field field, int x, int y) {
        GamePiece gamePiece = field.getTileGamePiece(x, y);
        if(gamePiece==null){
            super.render(batch, field, x, y);
        }
        else{
            batch.draw(region, gamePiece.bounds.x, gamePiece.bounds.y, gamePiece.bounds.width, gamePiece.bounds.height);
        }
    }
}
