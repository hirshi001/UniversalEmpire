package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
        displayPosition.set(gamePiece.getPosition());

        float size = 1F;
        float halfSize = size / 2F;
        float x = displayPosition.x - halfSize;
        float y = displayPosition.y - halfSize;
        batch.draw(region, x, y, size, size);
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        super.debugRender(renderer);
        renderer.rect(displayPosition.x-0.5F, displayPosition.y-0.5F, 1F, 1F);
    }
}
