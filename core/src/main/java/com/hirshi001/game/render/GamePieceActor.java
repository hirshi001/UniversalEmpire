package com.hirshi001.game.render;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.hirshi001.game.shared.game.GamePiece;

public abstract class GamePieceActor<T extends GamePiece> {

    protected T gamePiece;
    protected Vector2 displayPosition = new Vector2(), position = new Vector2();

    public GamePieceActor(T gamePiece){
        this.gamePiece = gamePiece;
    }

    public T getGamePiece(){
        return gamePiece;
    }

    public abstract void render(SpriteBatch batch, float delta);

    public void debugRender(ShapeRenderer renderer){
        renderer.setColor(Color.BLUE);
        renderer.rect(gamePiece.bounds.x, gamePiece.bounds.y, gamePiece.bounds.width, gamePiece.bounds.height);
        LinePath<Vector2> path = gamePiece.getProperties().get("path");
        if(path!=null){
            for(LinePath.Segment<Vector2> segment:path.getSegments()){
                renderer.line(segment.getBegin(), segment.getEnd());
            }
        }
    }

}
