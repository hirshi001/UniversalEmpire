package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Interpolation;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.shared.entities.CircleGamePiece;
import com.hirshi001.game.shared.entities.Fireball;

public class FireballActor extends GamePieceActor<Fireball> {


    TextureRegion region;
    Affine2 transform;

    public FireballActor(Fireball gamePiece) {
        super(gamePiece);
        region = GameApp.Game().gameResources.getFromAtlas("entities/bullet/Bullet");
        transform = new Affine2();
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        gamePiece.bounds.getPosition(position);
        float dst = position.dst(displayPosition);
        if (dst > 1f) {
            displayPosition.set(position);
        } else {
            displayPosition.interpolate(position, 0.5F, Interpolation.linear);
        }

        Number angle = gamePiece.getProperties().get("angle", 10F);
        transform.setToTranslation(displayPosition.x, displayPosition.y);
        transform.rotateRad(angle.floatValue());
        batch.draw(region, gamePiece.bounds.width, gamePiece.bounds.height, transform);
        // batch.draw(region, displayPosition.x, displayPosition.y, gamePiece.bounds.width, gamePiece.bounds.height);
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        super.debugRender(renderer);
        Number radius = gamePiece.getProperties().get("radius", 0.5F);
        renderer.setColor(Color.RED);
        renderer.circle(displayPosition.x, displayPosition.y , radius.floatValue(), 20);
    }
}
