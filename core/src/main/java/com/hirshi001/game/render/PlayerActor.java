package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.GameResources;
import com.hirshi001.game.shared.entities.Player;

public class PlayerActor extends GamePieceActor<Player> {

    float time;
    Animation<TextureRegion> animation;

    public PlayerActor(Player gamePiece) {
        super(gamePiece);
        GameResources resources = GameApp.Game().gameResources;
        Array<TextureRegion> regions = new Array<>(2);
        for (int i = 0; i < 2; i++) {
            regions.add(resources.getFromAtlas("entities/player/player-" + (i+1)));

        }
        animation = new Animation<>(0.25F, regions);
        time = MathUtils.random(0.25F);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        gamePiece.bounds.getPosition(position);
        float dst = position.dst(displayPosition);
        if (dst > 3F) {
            displayPosition.set(position);
        } else if (dst > 0.1f) {
            displayPosition.interpolate(position, 0.3F, Interpolation.linear);
        }

        time += delta*MathUtils.random(0.9F, 1.1F);
        TextureRegion region = animation.getKeyFrame(time, true);
        if (region != null) {
            float x = displayPosition.x + gamePiece.bounds.width / 2F - 1F;
            float y = displayPosition.y + gamePiece.bounds.height / 2F - 1F;
            batch.draw(region, x, y, 2F, 2F);
        }
    }


}
