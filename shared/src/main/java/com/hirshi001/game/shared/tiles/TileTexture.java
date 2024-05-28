package com.hirshi001.game.shared.tiles;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntIntMap;
import com.hirshi001.game.shared.util.PixmapUtil;

import java.util.ArrayDeque;
import java.util.Queue;


/**
 * A class that holds the textures of the tiles
 * Note: This class is not thread safe
 */
public class TileTexture {

    public Pixmap tiles;
    private final int tileSize;
    private final int columns = 10;
    private int rows = 10;

    private final IntIntMap tileIdToTextureId = new IntIntMap();
    private final Queue<Integer> freeTextureIds = new ArrayDeque<>();

    private boolean useTextureClass;
    private Texture texture;

    private boolean dirty;

    public TileTexture(int tileSize, boolean useTextureClass) {
        this.tileSize = tileSize;
        this.useTextureClass = useTextureClass;
        tiles = new Pixmap(tileSize * columns, tileSize * rows, Pixmap.Format.RGBA8888);
        for(int i = 0; i < columns * rows; i++){
            freeTextureIds.add(i);
        }
        if(useTextureClass) {
            texture = new Texture(tiles);
        }
    }

    private int register(int tileId) {
        Integer textureId = freeTextureIds.poll();
        if(textureId == null){
            resize(rows + 1);
            textureId = freeTextureIds.poll();
        }
        tileIdToTextureId.put(tileId, textureId);
        return textureId;
    }

    /**
     * Registers a tile with the given texture
     * @param tileId the id of the tile
     * @param texture the texture of the tile
     */
    public void register(int tileId, Pixmap texture) {
        int textureId = register(tileId);
        updateTexture(textureId, texture, 0, 0, texture.getWidth(), texture.getHeight());
    }

    /**
     * Registers a tile with the given bytes, and sets the texture region if useTextureClass is true
     * @param tile the tile to register and set the texture region
     * @param texture the texture of the tile
     */
    public void registerAndSetRegion(Tile tile, byte[] texture) {
        int textureId = register(tile.getID());
        updateTexture(textureId, texture);
        if(useTextureClass) {
            tile.texture = getRegion(tile.getID());
        }
    }


    public int getTextureId(int tileId) {
        return tileIdToTextureId.get(tileId, -1);
    }

    public TextureRegion getRegion(int tileId){
        int textureId = tileIdToTextureId.get(tileId, -1);
        if(textureId == -1) return null;
        return new TextureRegion(texture, x(textureId), y(textureId), tileSize, tileSize);
    }



    /**
     * Updates the texture of the tile with the given id
     * @param textureId the texture id of the tile
     * @param texture the new texture
     * @param srcX the x coordinate of the source texture
     * @param srcY the y coordinate of the source texture
     * @param srcWidth the width of the source texture
     * @param srcHeight the height of the source texture
     */
    public void updateTexture(int textureId, Pixmap texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        tiles.drawPixmap(texture, x(textureId), y(textureId), srcX, srcY, srcWidth, srcHeight);
        if(useTextureClass) {
            this.texture.draw(tiles, 0, 0);
        }
    }

    public void updateTexture(int textureId, byte[] texture) {
        PixmapUtil.setBytes(tiles, texture, x(textureId), y(textureId), tileSize, tileSize);
        if(useTextureClass) {
            this.texture.draw(tiles, 0, 0);
        }
    }

    /**
     * Sets the texture of the tile with the given id to the given bytes
     * @param tileId the id of the texture
     * @param bytes the bytes of the texture
     */
    public void setBytes(int tileId, byte[] bytes) {
        int textureId = tileIdToTextureId.get(tileId, -1);
        if(textureId == -1) return;
        PixmapUtil.setBytes(tiles, bytes, x(textureId), y(textureId), tileSize, tileSize);
    }

    /**
     * Gets the bytes of the texture with the given id
     * @param tileId the id of the texture
     * @return the bytes of the texture
     */
    public byte[] getBytes(int tileId) {
        int textureId = tileIdToTextureId.get(tileId, -1);
        if(textureId == -1) return null;
        return PixmapUtil.getBytes(tiles, x(textureId), y(textureId), tileSize, tileSize);
    }

    /**
     * Calculates the X coordinate of the texture in the tiles pixmap
     * @param id the id of the texture
     * @return X coordinate of the texture
     */
    private int x(int id){
        return (id % columns) * tileSize;
    }

    /**
     * Calculates the Y coordinate of the texture in the tiles pixmap
     * @param id the id of the texture
     * @return Y coordinate of the texture
     */
    private int y(int id){
        return (id / columns) * tileSize;
    }


    /**
     * Resizes the tiles pixmap to the given number of rows
     * @param newRows the new number of rows
     */
    public void resize(int newRows) {
        if(newRows <= rows) return;

        Pixmap newTiles = new Pixmap(tileSize * columns, tileSize * newRows, Pixmap.Format.RGBA8888);
        newTiles.drawPixmap(tiles, 0, 0);

        tiles = newTiles;
        if(useTextureClass) {
            texture.dispose();
            texture = new Texture(newTiles);
        }else{
            tiles.dispose();
        }

        for(int i = columns * rows; i < columns * newRows; i++){
            freeTextureIds.add(i);
        }
        rows = newRows;
        dirty = true;
    }

    public void remove(int tileId) {
        int textureId = tileIdToTextureId.remove(tileId, -1);
        if(textureId == -1) return;
        freeTextureIds.add(textureId);
    }

    public boolean dirty() {
        boolean dirty = this.dirty;
        this.dirty = false;
        return dirty;
    }
}
