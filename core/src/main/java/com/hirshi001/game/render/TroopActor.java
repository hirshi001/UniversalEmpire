package com.hirshi001.game.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dongbat.jbump.IntPoint;
import com.hirshi001.game.ClientField;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.shared.control.MoveTroopMovement;
import com.hirshi001.game.shared.control.Movement;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.SearchNode;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class TroopActor<T extends Troop> extends GamePieceActor<T>{
    public TroopActor(T gamePiece, FieldRender fieldRender) {
        super(gamePiece, fieldRender);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if(fieldRender.selectedItems.contains(gamePiece, false) && gamePiece.getControllerId() == ((ClientField)gamePiece.field).getControllerId()){
            // TODO: enable shader for highlighting
        }
    }

    @Override
    public void debugRender(ShapeRenderer renderer) {

        if(fieldRender.selectedItems.contains(gamePiece, false) && gamePiece.getControllerId() == ((ClientField)gamePiece.field).getControllerId()){
            renderer.setColor(Color.GREEN);
        }
        else if(gamePiece.getControllerId() == GameApp.field.getControllerId()) {
            renderer.setColor(Color.RED);
        }else{
            renderer.setColor(Color.BLUE);
        }
        // renderer.circle(gamePiece.getX(), gamePiece.getY(), 0.1F, 10);

        Movement movement = gamePiece.getMovement();
        if(movement instanceof MoveTroopMovement){
            MoveTroopMovement moveTroopMovement = (MoveTroopMovement) movement;
            LinkedList<IntPoint> path = moveTroopMovement.path;
            renderer.setColor(Color.YELLOW);

            Iterator<IntPoint> iterator = path.iterator();
            if(iterator.hasNext()) {
                IntPoint prev = iterator.next();
                renderer.line(gamePiece.getX(), gamePiece.getY(), prev.x + 0.5f, prev.y + 0.5f);
                while (iterator.hasNext()) {
                    IntPoint next = iterator.next();
                    renderer.line(prev.x + 0.5f, prev.y + 0.5f, next.x + 0.5f, next.y + 0.5f);
                    prev = next;
                }
            }

        }


    }
}
