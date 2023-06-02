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

    @SuppressWarnings("unchecked")
    public static void register() {
        GameResources resources = GameApp.gameResources;
        DEFAULT = register(new DefaultTileRenderer<>(Tiles.DEFAULT_TILE, getRegion(resources, "grass")));
        SNOW = register(new DefaultTileRenderer<>(Tiles.SNOW, getRegion(resources, "snow")));
        GRASS = register(new DefaultTileRenderer<>(Tiles.GRASS, getRegion(resources, "grass")));
        DIRT = register(new DefaultTileRenderer<>(Tiles.DIRT, getRegion(resources, "ground")));
        STONE = register(new StoneTileRenderer(Tiles.STONE, getRegion(resources, "wall")));
        SAND = register(new DefaultTileRenderer<>(Tiles.SAND, getRegion(resources, "grass")));
    }

    private static TextureRegion getRegion(GameResources resources, String name) {
        System.out.println("Loading texture: " + name);
        TextureRegion region = resources.getFromAtlas("tiles/" + name);
        System.out.println("Region: " + region);
        return region;
    }


    public static TileRenderer register(TileRenderer renderer) {
        TILE_RENDERERS.register(renderer, renderer.getTile().getID());
        return renderer;
    }


}
