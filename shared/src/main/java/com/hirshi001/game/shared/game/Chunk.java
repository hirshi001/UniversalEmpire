package com.hirshi001.game.shared.game;

import com.badlogic.gdx.math.Rectangle;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.util.HashedPoint;

import java.util.*;

public class Chunk{

    public final Rectangle bounds;
    public final HashedPoint chunkPosition;

    public final Set<GamePiece> items = new HashSet<>();
    public final List<GamePiece> itemsToAdd = new ArrayList<>();
    private final Tile[][] tiles;

    public Field field;
    private int chunkSize;

    public final Map<HashedPoint, GamePiece> tileGamePieces = new HashMap<>();
    private final HashedPoint tempPoint = new HashedPoint();

    public Chunk(int chunkSize, HashedPoint chunkPosition){
        this(chunkSize, chunkPosition, new Tile[chunkSize][chunkSize]);
    }

    public Chunk(int chunkSize, HashedPoint chunkPosition, Tile[][] tiles) {
        this.chunkSize = chunkSize;
        this.chunkPosition = chunkPosition;
        this.bounds = new Rectangle(getChunkX() * getChunkSize()-1, getChunkY() * getChunkSize()-1, getChunkSize()+2, getChunkSize()+2);
        this.tiles = tiles;
    }

    public void setTile(int col, int row, Tile tile) {
        tiles[col][row] = tile;
    }

    public void setTileEntity(int col, int row, GamePiece tileGamePiece) {
        tempPoint.set(col, row);
        tempPoint.recalculateHash();
        tileGamePiece.chunk = this;
        if(tileGamePieces.containsKey(tempPoint)) {
            tileGamePieces.put(tempPoint, tileGamePiece);
        }
        else tileGamePieces.put(new HashedPoint(col, row), tileGamePiece);
    }

    public Tile getTile(int col, int row) {
        return tiles[col][row];
    }

    public Tile getTileFromWorldCoords(int col, int row) {
        return tiles[col - getChunkX() * getChunkSize()][row - getChunkY() * getChunkSize()];
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean add(GamePiece item) {
        item.chunk = this;
        if(bounds.contains(item.getCenterX(), item.getCenterY())){
            items.add(item);
            return true;
        }
        return false;
    }

    public boolean addToItemsToAdd(GamePiece item) {
        item.chunk = this;
        if(bounds.contains(item.getCenterX(), item.getCenterY())){
            itemsToAdd.add(item);
            return true;
        }
        return false;
    }

    public boolean remove(GamePiece item) {
        return items.remove(item);
    }

    public int getChunkX() {
        return chunkPosition.x;
    }

    public int getChunkY() {
        return chunkPosition.y;
    }

    public int getChunkSize() {
        return chunkSize;
    }

}
