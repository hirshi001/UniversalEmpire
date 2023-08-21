package com.hirshi001.game.shared.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.tilepieces.TileGamePiece;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.util.HashedPoint;

import java.util.*;

public class Chunk {

    public final Rectangle bounds;
    public final HashedPoint chunkPosition;

    public final Set<GamePiece> items = new HashSet<>();
    public final List<GamePiece> itemsToAdd = new ArrayList<>();
    private final Tile[][] tiles;
    private final int[][] tileEntities;

    public Field field;
    private int chunkSize;

    // public final Map<HashedPoint, GamePiece> tileGamePieces = new HashMap<>();
    private final HashedPoint tempPoint = new HashedPoint();

    public Chunk(int chunkSize, HashedPoint chunkPosition) {
        this(chunkSize, chunkPosition, new Tile[chunkSize][chunkSize]);
    }

    public Chunk(int chunkSize, HashedPoint chunkPosition, Tile[][] tiles) {
        this.chunkSize = chunkSize;
        this.chunkPosition = chunkPosition;
        this.bounds = new Rectangle(getChunkX() * getChunkSize() - 1, getChunkY() * getChunkSize() - 1, getChunkSize() + 2, getChunkSize() + 2);
        this.tiles = tiles;
        this.tileEntities = new int[chunkSize][chunkSize];
    }

    public void setTile(int col, int row, Tile tile) {
        tiles[col][row] = tile;
    }

    public boolean setTileEntity(TileGamePiece tileGamePiece) {
        int chunkCol = tileGamePiece.x - getChunkX() * getChunkSize();
        int chunkRow = tileGamePiece.y - getChunkY() * getChunkSize();
        for (int c = 0; c < tileGamePiece.width; c++) {
            for (int r = 0; r < tileGamePiece.height; r++) {
                if (tileEntities[chunkCol + c][chunkRow + r] > 0) {
                    return false;
                }
            }
        }

        int col = tileGamePiece.x - getChunkX() * getChunkSize();
        int row = tileGamePiece.y - getChunkY() * getChunkSize();
        tileGamePiece.chunk = this;
        for (int c = 0; c < tileGamePiece.width; c++) {
            for (int r = 0; r < tileGamePiece.height; r++) {
                tileEntities[col + c][row + r] = tileGamePiece.getGameId();
            }
        }
        return true;
    }

    public void removeTileEntity(TileGamePiece tileGamePiece) {
        int col = tileGamePiece.x - getChunkX() * getChunkSize();
        int row = tileGamePiece.y - getChunkY() * getChunkSize();
        for (int c = 0; c < tileGamePiece.width; c++) {
            for (int r = 0; r < tileGamePiece.height; r++) {
                if(tileEntities[col + c][row + r] == tileGamePiece.getGameId())
                    tileEntities[col + c][row + r] = 0;
            }
        }
    }

    public int getTileEntityFromWorldCoords(int x, int y) {
        int col = x - getChunkX() * getChunkSize();
        int row = y - getChunkY() * getChunkSize();
        return tileEntities[col][row];
    }

    public int getTileEntity(int col, int row) {
        return tileEntities[col][row];
    }


    public Tile getTile(int col, int row) {
        return tiles[col][row];
    }

    public Tile getTileFromWorldCoords(int col, int row) {
        return tiles[col - getChunkX() * getChunkSize()][row - getChunkY() * getChunkSize()];
    }

    public boolean isWalkableFromWorldCoords(int x, int y){
        return isWalkable(x - getChunkX() * getChunkSize(), y - getChunkY() * getChunkSize());
    }

    public boolean isWalkable(int col, int row) {
        Tile tile = tiles[col][row];
        return tile!=null && !tile.isSolid && tileEntities[col][row] == 0;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int[][] getTileEntities(){
        return tileEntities;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean add(GamePiece item) {
        item.chunk = this;
        if (bounds.contains(item.getPosition())) {
            items.add(item);
            return true;
        }
        return false;
    }

    public boolean addToItemsToAdd(GamePiece item) {
        item.chunk = this;
        if (bounds.contains(item.getPosition())) {
            itemsToAdd.add(item);
            return true;
        }
        return false;
    }

    public Array<GamePiece> getGamePiecesFromWorldCoords(float x, float y, float w, float h, Array<GamePiece> gamePieces) {
        if(gamePieces == null)
            gamePieces = new Array<>();

        for(GamePiece item : items){
            if(item.getX() >= x && item.getX() <= x + w && item.getY() >= y && item.getY() <= y + h)
                gamePieces.add(item);
        }
        return gamePieces;
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
