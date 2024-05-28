package com.hirshi001.game.render.tilerenderers;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hirshi001.game.ClientField;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.registry.ID;
import com.hirshi001.game.shared.tiles.Tile;

public class TileRenderer {

    public void render(SpriteBatch batch, Field field, int x, int y) {
        Tile tile = field.getTile(x, y);
        if (tile == null) {
            return;
        }
        if(tile.texture == null) {
            ((ClientField)field).requestTileTexture(tile.getID());
            return;
        }
        batch.draw(tile.texture, x, y, 1, 1);
    }

}
