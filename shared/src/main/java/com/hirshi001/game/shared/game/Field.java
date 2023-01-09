package com.hirshi001.game.shared.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.World;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.game.shared.util.Resources;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Field extends World {

    private final int chunkSize;

    protected final Map<HashedPoint, Chunk> chunks = new ConcurrentHashMap<>();
    private int nextId;
    public long tick = 0;
    private final Map<Integer, GamePiece> gamePieces;
    private final Array<GamePiece> gamePiecesToAdd = new Array<>(), addedGamePieces = new Array<>(),
            gamePiecesToRemove = new Array<>(), removedGamePieces = new Array<>();
    private final Resources<HashedPoint> points = new Resources<>(5, HashedPoint::new);


    public Field(float cellSize, int chunkSize) {
        super(cellSize);
        this.chunkSize = chunkSize;
        gamePieces = new HashMap<>();
    }

    public Collection<Chunk> getChunks() {
        return chunks.values();
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public boolean addGamePiece(GamePiece gamePiece) {
        synchronized (gamePiecesToAdd) {
            gamePiece.setGameId(nextId);
            nextId++;
            gamePiecesToAdd.add(gamePiece);
            return true;
        }
    }

    public boolean addGamePiece(GamePiece gamePiece, int id) {
        synchronized (gamePiecesToAdd) {
            gamePiece.setGameId(id);
            gamePiecesToAdd.add(gamePiece);
            return true;
        }
    }

    public GamePiece getGamePiece(int id) {
        return gamePieces.get(id);
    }

    public boolean removeGamePiece(int id) {
        synchronized (gamePiecesToRemove) {
            return removeGamePiece(gamePieces.get(id));
        }
    }

    public boolean removeGamePiece(GamePiece gamePiece) {
        if (gamePiece == null) return false;
        synchronized (gamePiecesToRemove) {
            gamePiecesToRemove.add(gamePiece);
        }
        return true;
    }

    protected boolean remove0(GamePiece gamePiece) {
        try {
            remove(gamePiece);
        } catch (Exception ignored) {
        } //catch and ignore if a game piece is already removed
        if (gamePiece.chunk != null) gamePiece.chunk.remove(gamePiece);
        return gamePieces.remove(gamePiece.getGameId()) != null;
    }

    protected void add0(GamePiece gamePiece, int i) {
        HashedPoint temp = points.get();
        gamePieces.put(i, gamePiece);
        gamePiece.setGameId(i);
        temp.set(MathUtils.floor(gamePiece.getCenterX() / getChunkSize()), MathUtils.floor(gamePiece.getCenterY() / getChunkSize()));
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        if (chunk == null) {
            return;
        }
        chunk.add(gamePiece);

        if (gamePiece.worldInteractable()) {
            add(gamePiece, gamePiece.bounds.x, gamePiece.bounds.y, gamePiece.bounds.width, gamePiece.bounds.height);
        }
        gamePiece.setField(this);
        points.release(temp);
    }


    public boolean containsChunk(int chunkX, int chunkY) {
        HashedPoint temp = points.get();
        temp.set(chunkX, chunkY);
        temp.recalculateHash();
        boolean contains = containsChunk(temp);
        points.release(temp);
        return contains;
    }

    public Chunk getChunk(int chunkX, int chunkY) {
        HashedPoint temp = points.get();
        temp.set(chunkX, chunkY);
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        points.release(temp);
        return chunk;
    }

    public Chunk getChunk(Point point) {
        return chunks.get(point);
    }

    public Chunk addChunk(int chunkX, int chunkY) {
        HashedPoint temp = points.get();
        temp.set(chunkX, chunkY);
        temp.recalculateHash();
        if (containsChunk(temp)) {
            Chunk chunk = chunks.get(temp);
            points.release(temp);
            return chunk;
        }
        return addChunk(loadChunk(chunkX, chunkY));
    }

    public abstract Chunk loadChunk(int chunkX, int chunkY);

    public Chunk getChunkFromCoord(float x, float y) {
        HashedPoint temp = points.get();
        temp.set(MathUtils.floor(x / getChunkSize()), MathUtils.floor(y / getChunkSize()));
        Chunk chunk = chunks.get(temp);
        points.release(temp);
        return chunk;
    }

    public Chunk getChunkFromCoord(int x, int y) {
        HashedPoint temp = points.get();
        if (x < 0) temp.x = (x + 1) / getChunkSize() - 1;
        else temp.x = x / getChunkSize();
        if (y < 0) temp.y = (y + 1) / getChunkSize() - 1;
        else temp.y = y / getChunkSize();
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        points.release(temp);
        return chunk;
    }

    public boolean removeChunk(int chunkX, int chunkY) {
        HashedPoint temp = points.get();
        temp.set(chunkX, chunkY);
        temp.recalculateHash();
        boolean success = removeChunk(temp);
        points.release(temp);
        return success;
    }

    public boolean containsChunk(Point chunk) {
        return chunks.containsKey(chunk);
    }

    public void setTile(int x, int y, Tile tile) {
        Chunk chunk = getChunkFromCoord(x, y);
        if (chunk == null) return;
        chunk.setTile(x - chunk.getChunkX() * getChunkSize(), y - chunk.getChunkY() * getChunkSize(), tile);
    }

    public void setTile(HashedPoint point, Tile tile) {
        setTile(point.x, point.y, tile);
    }

    public void setTileGamePiece(int x, int y, GamePiece piece) {
        Chunk chunk = getChunkFromCoord(x, y);
        if (chunk == null) return;
        chunk.setTileEntity(x - chunk.getChunkX() * getChunkSize(), y - chunk.getChunkY() * getChunkSize(), piece);
    }

    public GamePiece getTileGamePiece(int x, int y) {
        Chunk chunk = getChunkFromCoord(x, y);
        if (chunk == null) {
            return null;
        }
        HashedPoint temp = points.get();
        temp.set(x - chunk.getChunkX() * chunk.getChunkSize(), y - chunk.getChunkY() * chunk.getChunkSize());
        temp.recalculateHash();
        GamePiece piece = chunk.tileGamePieces.get(temp);
        points.release(temp);
        return piece;
    }

    public Chunk addChunk(Chunk chunk) {
        if (chunk == null) return null;
        chunks.put(chunk.chunkPosition, chunk);
        chunk.field = this;
        for (GamePiece gamePiece : chunk.items) {
            addGamePiece(gamePiece, gamePiece.getGameId());
        }
        return chunk;
    }

    public boolean removeChunk(Point chunk) {
        Chunk c = chunks.remove(chunk);
        if (c == null) return false;
        for (GamePiece gamePiece : c.items) {
            removeGamePiece(gamePiece);
        }
        return true;
    }

    public Chunk relocateGamePiece(GamePiece item, Chunk original) {
        HashedPoint temp = points.get();
        getChunkPosition(item.getCenterX(), item.getCenterY(), temp);
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        if (chunk == null) {
            if(isServer() && item.shouldLoadChunk()){
                addChunk(temp.x, temp.y);
            }
            else {
                removeGamePiece(item);
                return null;
            }
        }
        points.release(temp);
        if(chunk!=null) chunk.add(item);
        return chunk;
    }

    public Point getChunkPosition(float x, float y) {
        Point p = new Point();
        getChunkPosition(x, y, p);
        return p;
    }


    public void getChunkPosition(float x, float y, Point out) {
        out.x = MathUtils.floor(x / chunkSize);
        out.y = MathUtils.floor(y / chunkSize);
    }

    public void tick(float delta) {
        tick++;

        for (GamePiece gamePiece : getItems()) {
            gamePiece.tick(delta);
        }
        for (Chunk chunk : getChunks()) {
            relocation(chunk);
        }
        synchronized (gamePiecesToAdd) {
            addedGamePieces.clear();
            addedGamePieces.addAll(gamePiecesToAdd);
            gamePiecesToAdd.clear();
        }
        for (GamePiece gamePiece : addedGamePieces) {
            add0(gamePiece, gamePiece.getGameId());
        }

        synchronized (gamePiecesToRemove) {
            removedGamePieces.clear();
            removedGamePieces.addAll(gamePiecesToRemove);
            gamePiecesToRemove.clear();
        }

        for (GamePiece gamePiece : removedGamePieces) {
            remove0(gamePiece);
        }

    }

    private void relocation(Chunk chunk) {
        //use an iterator on items
        Iterator<GamePiece> iterator = chunk.items.iterator();
        while (iterator.hasNext()) {
            GamePiece gamePiece = iterator.next();
            if (!chunk.bounds.contains(gamePiece.getCenterX(), gamePiece.getCenterY())) {
                if (relocateGamePiece(gamePiece, chunk) != chunk) {
                    iterator.remove();
                }
            }
        }
    }


    @Override
    public Set<GamePiece> getItems() {
        return (Set<GamePiece>) super.getItems();
    }

    public boolean isServer() {
        return true;
    }

}
