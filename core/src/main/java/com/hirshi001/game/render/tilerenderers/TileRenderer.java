package com.hirshi001.game.render.tilerenderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.registry.ID;
import com.hirshi001.game.shared.tiles.Tile;

public abstract class TileRenderer<T extends Tile> implements ID {

    private int id;
    private T tile;

    public TileRenderer(T tile) {
        this.tile = tile;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    public T getTile() {
        return tile;
    }

    public abstract void render(SpriteBatch batch, Field field, int x, int y);
}
