package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.GameResources;
import com.hirshi001.game.shared.entities.Knight;

public class KnightActor extends GamePieceActor<Knight> {


    float time;
    Animation<TextureRegion> animation;



    public KnightActor(Knight gamePiece) {
        super(gamePiece);

        GameResources resources = GameApp.Game().gameResources;
        Array<TextureRegion> regions = new Array<>(7);
        for (int i = 0; i < 3; i++) {
            regions.add(resources.getFromAtlas("entities/knight/woodKnight" + (i+1)));

        }
        animation = new Animation<>(0.20F, regions, Animation.PlayMode.LOOP_PINGPONG);
        time = MathUtils.random(0.20F);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        float lastX = displayPosition.x;
        gamePiece.bounds.getPosition(position);
        float dst = position.dst(displayPosition);
        if (dst > 3F) {
            displayPosition.set(position);
        } else if (dst > 0.1f) {
            displayPosition.interpolate(position, 0.3F, Interpolation.linear);
        }
        boolean facingLeft = lastX > displayPosition.x;

        time += delta*MathUtils.random(0.9F, 1.1F);
        TextureRegion region = animation.getKeyFrame(time, true);
        if (region != null) {
            float x = displayPosition.x + gamePiece.bounds.width / 2F - 1F;
            float y = displayPosition.y + gamePiece.bounds.height / 2F - 1F;
            if(facingLeft) batch.draw(region, x, y, 2F, 2F);
            else batch.draw(region, x+2F, y, -2F, 2F);
        }
    }
}
