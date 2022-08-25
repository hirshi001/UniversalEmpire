package com.hirshi001.game.render.tilerenderers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.GameResources;
import com.hirshi001.game.shared.registry.DefaultRegistry;
import com.hirshi001.game.shared.registry.Registry;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;

public class TileRenderers {

    public static final Registry<TileRenderer> TILE_RENDERERS = new DefaultRegistry<>();

    public static TileRenderer<Tile>
    DEFAULT,
    SNOW,
    GRASS,
    DIRT,
    STONE,
    SAND;

    public static void register(GameApp app){
        GameResources resources = app.gameResources;
        DEFAULT = register(new DefaultTileRenderer<>(Tiles.DEFAULT_TILE, new TextureRegion((Texture) resources.get("default"))));
        SNOW = register(new DefaultTileRenderer<>(Tiles.SNOW, new TextureRegion((Texture) resources.get("snow"))));
        GRASS = register(new DefaultTileRenderer<>(Tiles.GRASS, new TextureRegion((Texture) resources.get("grass"))));
        DIRT = register(new DefaultTileRenderer<>(Tiles.DIRT, new TextureRegion((Texture) resources.get("ground"))));
        STONE = register(new StoneTileRenderer(Tiles.STONE, new TextureRegion((Texture) resources.get("wall"))));
        SAND = register(new DefaultTileRenderer<>(Tiles.SAND, new TextureRegion((Texture) resources.get("grass"))));

    }


    public static TileRenderer register(TileRenderer renderer) {
        TILE_RENDERERS.register(renderer, renderer.getTile().getID());
        return renderer;
    }


}
