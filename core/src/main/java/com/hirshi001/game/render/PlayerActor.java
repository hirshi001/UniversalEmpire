package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.shared.entities.Player;

public class PlayerActor extends GamePieceActor<Player>{

    float time = 0F;
    Animation<TextureRegion> animation;

    public PlayerActor(Player gamePiece) {
        super(gamePiece);
        TextureAtlas atlas = GameApp.Game().atlas;
        animation = new Animation<>(1F, atlas.findRegion("wizardIdle1"), atlas.findRegion("wizardIdle2"));
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        gamePiece.bounds.getPosition(position);
        float dst = position.dst(displayPosition);
        if(dst > 3F){
            displayPosition.set(position);
        }else if(dst > 0.05f){
            displayPosition.interpolate(position, 0.5F, Interpolation.linear);
        }
        time+=delta;
        TextureRegion region = animation.getKeyFrame(time, true);
        if(region!=null) {
            float x = displayPosition.x + gamePiece.bounds.width/2F - 1F;
            float y = displayPosition.y + gamePiece.bounds.height/2F- 1F;
            batch.draw(region, x, y, 2F, 2F);
        }
    }


}
