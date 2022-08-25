package com.hirshi001.game.shared.tiles;


import com.hirshi001.game.shared.registry.DefaultRegistry;
import com.hirshi001.game.shared.registry.Registry;

public class Tiles {

    public static final Registry<Tile> TILE_REGISTRY = new DefaultRegistry<>();

    public static Tile
    DEFAULT_TILE,
    SNOW,
    GRASS,
    DIRT,
    STONE,
    SAND;

    public static void register(){
        DEFAULT_TILE = register(new Tile(), 0);
        SNOW = register(new Tile(), 1);
        GRASS = register(new Tile(), 2);
        DIRT = register(new Tile(), 3);
        STONE = register(new Tile(), 4);
        SAND = register(new Tile(), 5);
    }


    public static Tile register(Tile tile, int id) {
        TILE_REGISTRY.register(tile, id);
        return tile;
    }


}
