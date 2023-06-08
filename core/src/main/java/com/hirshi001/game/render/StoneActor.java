package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.shared.entities.Stone;

public class StoneActor extends GamePieceActor<Stone>{

    private final TextureRegion region;
    public StoneActor(Stone gamePiece, FieldRender fieldRender) {
        super(gamePiece, fieldRender);
        region = GameApp.gameResources.getFromAtlas("resources/rock");
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        displayPosition.set(gamePiece.bounds.x, gamePiece.bounds.y);

        float size = 1F;
        float halfSize = size / 2F;
        float x = displayPosition.x + gamePiece.bounds.width / 2F - halfSize;
        float y = displayPosition.y + gamePiece.bounds.height / 2F - halfSize;
        batch.draw(region, x, y, size, size);
    }
}
