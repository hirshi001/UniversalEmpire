package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.GameResources;
import com.hirshi001.game.shared.entities.troop.Knight;

public class KnightActor extends TroopActor<Knight> {


    float time;
    static Animation<TextureRegion> woodAnimation;

    public static void loadAnimation() {
        woodAnimation = loadAnimation("wood");
    }

    private static Animation<TextureRegion> loadAnimation(String type) {
        GameResources resources = GameApp.Game().gameResources;
        int regionCount = 7;
        Array<TextureRegion> regions = new Array<>(regionCount);
        for (int i = 0; i < regionCount; i++) {
            regions.add(resources.getFromAtlas("entities/knight/" + type + "/" + type + "Knight" + (i + 1)));
        }
        return new Animation<>(0.1f, regions, Animation.PlayMode.LOOP_PINGPONG);
    }


    public KnightActor(Knight gamePiece, FieldRender fieldRender) {
        super(gamePiece, fieldRender);

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

        time += delta * MathUtils.random(0.9F, 1.1F);
        TextureRegion region = getAnimation().getKeyFrame(time);

        if (region != null) {
            float x = displayPosition.x + gamePiece.bounds.width / 2F - 1F;
            float y = displayPosition.y + gamePiece.bounds.height / 2F - 1F;
            if (facingLeft) batch.draw(region, x, y, 2F, 2F);
            else batch.draw(region, x + 2F, y, -2F, 2F);
            super.render(batch, delta);
        }
    }

    private Animation<TextureRegion> getAnimation(){
        return woodAnimation;
    }
}
