package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.hirshi001.game.shared.entities.CircleGamePiece;

public class CircleGamePieceActor extends GamePieceActor<CircleGamePiece> {

    public CircleGamePieceActor(CircleGamePiece gamePiece) {
        super(gamePiece);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        gamePiece.bounds.getPosition(position);
        float dst = position.dst(displayPosition);
        if (dst > 1f) {
            displayPosition.set(position);
        } else {
            displayPosition.interpolate(position, 0.1F, Interpolation.linear);
        }
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        super.debugRender(renderer);
        System.out.println("rendering circle debug");
        Number radius = gamePiece.getProperties().get("radius", 1F);
        renderer.circle(displayPosition.x+radius.floatValue(), displayPosition.y+ radius.floatValue(), radius.floatValue());
    }
}
