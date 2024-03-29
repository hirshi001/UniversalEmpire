package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.shared.entities.CircleGamePiece;
import com.hirshi001.game.shared.entities.Fireball;

public class FireballActor extends GamePieceActor<Fireball> {


    TextureRegion region;
    Affine2 transform;

    public FireballActor(Fireball gamePiece, FieldRender fieldRender) {
        super(gamePiece, fieldRender);
        region = GameApp.Game().gameResources.getFromAtlas("entities/arrow/arrow");
        transform = new Affine2();
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        position.set(gamePiece.getPosition());
        float dst = position.dst(displayPosition);
        if (dst > 1f) {
            displayPosition.set(position);
        } else {
            displayPosition.interpolate(position, 0.5F, Interpolation.linear);
        }

        Number angle = gamePiece.getProperties().get("angle", 0F);
        transform.setToTranslation(displayPosition.x-0.25F, displayPosition.y-0.25F);
        transform.rotateRad(angle.floatValue() - MathUtils.PI/4);
        batch.draw(region, 0.5F, 0.5F, transform);
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        super.debugRender(renderer);
        Number radius = gamePiece.getProperties().get("radius", 0.5F);
        renderer.setColor(Color.RED);
        renderer.circle(displayPosition.x, displayPosition.y , radius.floatValue(), 20);
    }
}
