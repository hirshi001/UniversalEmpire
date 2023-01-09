package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Interpolation;
import com.hirshi001.game.shared.entities.CircleGamePiece;
import com.hirshi001.game.shared.entities.Fireball;

public class FireballActor extends GamePieceActor<Fireball> {


    static TextureRegion region;

    static{
        int size = 64;
        Pixmap pixmap = new Pixmap(size*2, size*2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.YELLOW);
        pixmap.fillCircle(size, size, size);
        region = new TextureRegion(new Texture(pixmap));
    }

    public FireballActor(Fireball gamePiece) {
        super(gamePiece);
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
        batch.draw(region, displayPosition.x, displayPosition.y, gamePiece.bounds.width, gamePiece.bounds.height);
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {
        super.debugRender(renderer);
        Number radius = gamePiece.getProperties().get("radius", 0.5F);
        renderer.setColor(Color.RED);
        renderer.circle(displayPosition.x-radius.floatValue(), displayPosition.y- radius.floatValue(), radius.floatValue(), 20);
    }
}
