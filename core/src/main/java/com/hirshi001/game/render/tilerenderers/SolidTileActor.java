package com.hirshi001.game.render.tilerenderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hirshi001.game.render.FieldRender;
import com.hirshi001.game.render.GamePieceActor;
import com.hirshi001.game.shared.entities.SolidTile;

public class SolidTileActor extends GamePieceActor<SolidTile> {
    public SolidTileActor(SolidTile gamePiece, FieldRender fieldRender) {
        super(gamePiece, fieldRender);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        // no render
    }
}
