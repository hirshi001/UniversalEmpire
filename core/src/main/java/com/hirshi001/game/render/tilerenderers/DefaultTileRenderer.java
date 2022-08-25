package com.hirshi001.game.render.tilerenderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.tiles.Tile;

public class DefaultTileRenderer<T extends Tile> extends TileRenderer<T> {

    public final TextureRegion region;

    public DefaultTileRenderer(T tile, TextureRegion region) {
        super(tile);
        this.region = region;
    }

    @Override
    public void render(SpriteBatch batch, Field field, int x, int y) {
        batch.draw(region, x, y, 1, 1);
    }
}
