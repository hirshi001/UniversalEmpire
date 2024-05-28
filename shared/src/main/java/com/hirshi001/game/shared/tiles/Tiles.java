package com.hirshi001.game.shared.tiles;


import com.badlogic.gdx.graphics.Pixmap;
import com.hirshi001.game.shared.registry.MapRegistry;
import com.hirshi001.game.shared.registry.Registry;

public class Tiles {

    private static Tiles INSTANCE;

    public static Tiles getInstance(){
        return INSTANCE;
    }

    public static void setInstance(Tiles tiles){
        INSTANCE = tiles;
    }

    public final Registry<Tile> tileRegistry = new MapRegistry<>();
    private final TileTexture tileTexture;
    private int nextID = 0;




    /**
     * Creates a new Tiles object with the given tile size
     * @param tileSize the size of the tiles
     * @param useTextureClass whether to use the Texture class
     */
    public Tiles(int tileSize, boolean useTextureClass) {
        tileTexture = new TileTexture(tileSize, useTextureClass);
    }

    /**
     * Gets the texture of the tile with the given id
     * @param tile the tile
     * @param texture the texture
     * @param id the id of the texture
     * @return the registered tile
     */
    public Tile register(Tile tile, Pixmap texture, int id) {
        tileTexture.register(id, texture);
        tileRegistry.register(tile, id);
        return tile;
    }


    /**
     * Registers a tile with the given texture
     * @param tile the tile to register
     * @param texture the texture of the tile
     * @return the registered tile
     */
    public Tile register(Tile tile, Pixmap texture) {
        while (tileRegistry.get(nextID) != null) {
            nextID++;
        }
        return register(tile, texture, nextID);
    }

    public TileTexture getTileTexture() {
        return tileTexture;
    }

    public Tile getOrRegister(int id) {
        Tile tile = tileRegistry.get(id);
        if(tile == null) {
            tile = new Tile();
            tile.setID(id);
            tileRegistry.register(tile, id);
        }
        return tile;
    }

}
