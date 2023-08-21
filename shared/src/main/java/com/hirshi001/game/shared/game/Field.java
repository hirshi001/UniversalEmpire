package com.hirshi001.game.shared.game;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.IntPoint;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.SolidTile;
import com.hirshi001.game.shared.entities.tilepieces.TileGamePiece;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.game.shared.util.PathFinder;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.game.shared.util.ThreadSafe;
import com.hirshi001.restapi.ScheduledExec;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Field {

    private final int chunkSize;

    protected final Map<HashedPoint, Chunk> chunks = new ConcurrentHashMap<>();
    private int nextId = 1; // start id at 1 so that 0 can be used to represent no id making it easier to initialize arrays which use ids
    public long time;
    private final Map<Integer, GamePiece> gamePieces;
    private final Array<GamePiece> gamePiecesToAdd = new Array<>(), addedGamePieces = new Array<>(),
            gamePiecesToRemove = new Array<>(), removedGamePieces = new Array<>();
    private final Pool<HashedPoint> points;
    public final GameMechanics gameMechanics;
    private final ScheduledExec exec;

    public Field(int chunkSize, GameMechanics gameMechanics, ScheduledExec exec) {
        this.chunkSize = chunkSize;
        this.gameMechanics = gameMechanics;
        this.exec = exec;
        gameMechanics.setField(this);
        gamePieces = new ConcurrentHashMap<>();
        points = new Pool<HashedPoint>() {
            @Override
            protected HashedPoint newObject() {
                return new HashedPoint();
            }
        };

    }

    public ScheduledExec getExec() {
        return exec;
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

    public GameMechanics getGameMechanics() {
        return gameMechanics;
    }

    public Array<GamePiece> queryRect(float x, float y, float w, float h, Array<GamePiece> gamePieces) {
        // get all chunks in the rectangle
        if (gamePieces == null) gamePieces = new Array<>();
        Point startChunk = getChunkPosition(x, y);
        Point endChunk = getChunkPosition(x + w, y + h);
        for (int i = startChunk.getX(); i <= endChunk.getX(); i++) {
            for (int j = startChunk.getY(); j <= endChunk.getY(); j++) {
                Chunk chunk = getChunk(i, j);
                if (chunk != null) {
                    chunk.getGamePiecesFromWorldCoords(x, y, w, h, gamePieces);
                }
            }
        }
        return gamePieces;
    }

    public void moveGamePieceShort(GamePiece gamePiece, float x2, float y2) {
        float x1 = gamePiece.getX();
        float y1 = gamePiece.getY();
        int tileX = (int) Math.floor(gamePiece.getX());
        int tileY = (int) Math.floor(gamePiece.getY());
        int targetTileX = (int) Math.floor(x2);
        int targetTileY = (int) Math.floor(y2);

        if (isServer()) {
            gamePiece.getProperties().put("dx", targetTileX - tileX);
            gamePiece.getProperties().put("dy", targetTileY - tileY);
        }

        if (tileX == targetTileX && tileY == targetTileY) {
            gamePiece.setPosition(x2, y2);
            return;
        }

        float d = 1e-5F;

        if (tileX == targetTileX) {
            // check top and bottom
            if (isWalkable(tileX, targetTileY)) {
                gamePiece.setPosition(x2, y2);
            } else if (tileY < targetTileY) { // collides with top tile
                gamePiece.setPosition(x2, targetTileY - d);
            } else { // collides with bottom tile
                gamePiece.setPosition(x2, tileY + d);
            }
            return;
        } else if (tileY == targetTileY) {
            // check left and right
            if (isWalkable(targetTileX, tileY)) {
                gamePiece.setPosition(x2, y2);
            } else if (tileX < targetTileX) { // collision with right tile
                gamePiece.setPosition(targetTileX - d, y2);
            } else { // collision with left tile
                gamePiece.setPosition(tileX + d, y2);
            }
        }
        // check corners
        // check top left
        else if (tileX > targetTileX && tileY < targetTileY) {
            boolean leftWalkable = isWalkable(tileX - 1, tileY);
            boolean topWalkable = isWalkable(tileX, tileY + 1);
            boolean topLeftWalkable = isWalkable(tileX - 1, tileY + 1);
            if (leftWalkable && topWalkable && topLeftWalkable) {
                gamePiece.setPosition(x2, y2);
                return;
            }
            // check top
            if (Intersector.intersectSegments(x1, y1, x2, y2, tileX, targetTileY, tileX + 1, targetTileY, null)) {
                if (!topWalkable) {
                    gamePiece.setY(targetTileY - d);
                    if (leftWalkable) {
                        gamePiece.setX(x2);
                    } else {
                        gamePiece.setX(tileX + d);
                    }
                    return;
                }
                if (topLeftWalkable) {
                    gamePiece.setPosition(x2, y2);
                    return;
                }
                gamePiece.setPosition(tileX + d, y2);
                return;
            } else { // check left
                if (!leftWalkable) {
                    gamePiece.setX(tileX + d);
                    if (topWalkable) {
                        gamePiece.setY(y2);
                    } else {
                        gamePiece.setY(targetTileY - d);
                    }
                    return;
                }
                if (topLeftWalkable) {
                    gamePiece.setPosition(x2, y2);
                    return;
                }
                gamePiece.setPosition(x2, targetTileY - d);
                return;
            }
        }
        // check top right
        else if (tileX < targetTileX && tileY < targetTileY) {
            boolean rightWalkable = isWalkable(tileX + 1, tileY);
            boolean topWalkable = isWalkable(tileX, tileY + 1);
            boolean topRightWalkable = isWalkable(tileX + 1, tileY + 1);
            if (rightWalkable && topWalkable && topRightWalkable) {
                gamePiece.setPosition(x2, y2);
                return;
            }
            // check top
            if (Intersector.intersectSegments(x1, y1, x2, y2, tileX, tileY + 1, tileX + 1, tileY + 1, null)) {
                if (!topWalkable) {
                    gamePiece.setY(targetTileY - d);
                    if (rightWalkable) {
                        gamePiece.setX(x2);
                    } else {
                        gamePiece.setX(tileX - d);
                    }
                    return;
                }
                if (topRightWalkable) {
                    gamePiece.setPosition(x2, y2);
                    return;
                }
                gamePiece.setPosition(tileX - d, y2);
                return;
            } else { // check right
                if (!rightWalkable) {
                    gamePiece.setX(tileX - d);
                    if (topWalkable) {
                        gamePiece.setY(y2);
                    } else {
                        gamePiece.setY(targetTileY - d);
                    }
                    return;
                }
                if (topRightWalkable) {
                    gamePiece.setPosition(x2, y2);
                    return;
                }
                gamePiece.setPosition(x2, targetTileY - d);
                return;
            }
        }
        // check bottom right
        else if (tileX < targetTileX) {
            boolean rightWalkable = isWalkable(tileX + 1, tileY);
            boolean bottomWalkable = isWalkable(tileX, tileY - 1);
            boolean bottomRightWalkable = isWalkable(tileX + 1, tileY - 1);
            if (rightWalkable && bottomWalkable && bottomRightWalkable) {
                gamePiece.setPosition(x2, y2);
                return;
            }
            // check bottom
            if (Intersector.intersectSegments(x1, y1, x2, y2, tileX, tileY, tileX + 1, tileY, null)) {
                if (!bottomWalkable) {
                    gamePiece.setY(tileY + d);
                    if (rightWalkable) {
                        gamePiece.setX(x2);
                    } else {
                        gamePiece.setX(targetTileX - d);
                    }
                    return;
                }
                if (bottomRightWalkable) {
                    gamePiece.setPosition(x2, y2);
                    return;
                }
                gamePiece.setPosition(targetTileX - d, y2);
                return;
            } else { // check right
                if (!rightWalkable) {
                    gamePiece.setPosition(targetTileX - d, y2);
                    if (bottomWalkable) {
                        gamePiece.setY(y2);
                    } else {
                        gamePiece.setY(tileY + d);
                    }
                    return;
                }
                if (bottomRightWalkable) {
                    gamePiece.setPosition(x2, y2);
                    return;
                }
                gamePiece.setPosition(x2, tileY + d);
                return;
            }
        }
        // check bottom left
        else {
            boolean leftWalkable = isWalkable(tileX - 1, tileY);
            boolean bottomWalkable = isWalkable(tileX, tileY - 1);
            boolean bottomLeftWalkable = isWalkable(tileX - 1, tileY - 1);
            if (leftWalkable && bottomWalkable && bottomLeftWalkable) {
                gamePiece.setPosition(x2, y2);
                return;
            }
            // check bottom
            if (Intersector.intersectSegments(x1, y1, x2, y2, tileX, tileY, tileX + 1, tileY, null)) {
                if (!bottomWalkable) {
                    gamePiece.setY(tileY + d);
                    if (leftWalkable) {
                        gamePiece.setX(x2);
                    } else {
                        gamePiece.setX(targetTileX + d);
                    }
                    return;
                }
                if (bottomLeftWalkable) {
                    gamePiece.setPosition(x2, y2);
                    return;
                }
                gamePiece.setPosition(targetTileX + d, y2);
                return;
            } else { // check left
                if (!leftWalkable) {
                    gamePiece.setPosition(tileX + d, y2);
                    if (bottomWalkable) {
                        gamePiece.setY(y2);
                    } else {
                        gamePiece.setY(tileY + d);
                    }
                    return;
                }
                if (bottomLeftWalkable) {
                    gamePiece.setPosition(x2, y2);
                    return;
                }
                gamePiece.setPosition(x2, tileY + d);
                return;
            }
        }


        // gamePiece.setPosition(x2, y2);
    }


    public LinkedList<IntPoint> findPathList(int startX, int startY, int endX, int endY) {
        return PathFinder.findPathList(this, startX, startY, endX, endY, null);
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

    @ThreadSafe
    public boolean removeGamePiece(GamePiece gamePiece) {
        if (gamePiece == null) return false;
        synchronized (gamePiecesToRemove) {
            gamePiecesToRemove.add(gamePiece);
        }
        return true;
    }

    @ThreadSafe
    protected boolean remove0(GamePiece gamePiece) {
        try {
            // remove(gamePiece);
            gamePiece.removed();
        } catch (Exception ignored) {
        } //catch and ignore if a game piece is already removed
        if (gamePiece.chunk != null) {
            gamePiece.chunk.remove(gamePiece);
            if (gamePiece instanceof TileGamePiece) {
                gamePiece.chunk.removeTileEntity((TileGamePiece) gamePiece);
            }
        }

        return gamePieces.remove(gamePiece.getGameId()) != null;
    }

    protected void add0(GamePiece gamePiece, int i) {
        HashedPoint temp = points.obtain();
        add0(gamePiece, i, temp);
        points.free(temp);
    }

    @ThreadSafe
    protected void add0(GamePiece gamePiece, int i, HashedPoint temp) {
        gamePieces.put(i, gamePiece);
        gamePiece.setGameId(i);
        temp.set(MathUtils.floor(gamePiece.getX() / getChunkSize()), MathUtils.floor(gamePiece.getY() / getChunkSize()));
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        if (chunk == null) {
            return;
        }
        chunk.add(gamePiece); // it's okay if the game piece is already in the chunk
        if (gamePiece instanceof TileGamePiece) {
            chunk.setTileEntity((TileGamePiece) gamePiece);
        }
        gamePiece.setField(this);
    }


    public boolean containsChunk(int chunkX, int chunkY) {
        HashedPoint temp = points.obtain();
        temp.set(chunkX, chunkY);
        temp.recalculateHash();
        boolean contains = containsChunk(temp);
        points.free(temp);
        return contains;
    }

    public Chunk getChunk(int chunkX, int chunkY) {
        HashedPoint temp = points.obtain();
        temp.set(chunkX, chunkY);
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        points.free(temp);
        return chunk;
    }

    @ThreadSafe
    public Chunk getChunk(Point point) {
        return chunks.get(point);
    }

    public Chunk addChunk(int chunkX, int chunkY) {
        HashedPoint temp = points.obtain();
        temp.set(chunkX, chunkY);
        temp.recalculateHash();
        if (containsChunk(temp)) {
            Chunk chunk = chunks.get(temp);
            points.free(temp);
            return chunk;
        }
        points.free(temp);
        return addChunk(loadChunk(chunkX, chunkY));
    }

    public Chunk addChunk(Point point) {
        if (containsChunk(point)) {
            return chunks.get(point);
        }
        return addChunk(loadChunk(point.x, point.y));
    }

    public abstract Chunk loadChunk(int chunkX, int chunkY);

    public Chunk getChunkFromCoord(float x, float y) {
        HashedPoint temp = points.obtain();
        temp.set(MathUtils.floor(x / getChunkSize()), MathUtils.floor(y / getChunkSize()));
        Chunk chunk = chunks.get(temp);
        points.free(temp);
        return chunk;
    }


    public Chunk getChunkFromCoord(int x, int y) {
        HashedPoint temp = points.obtain();
        if (x < 0) temp.x = (x + 1) / getChunkSize() - 1;
        else temp.x = x / getChunkSize();
        if (y < 0) temp.y = (y + 1) / getChunkSize() - 1;
        else temp.y = y / getChunkSize();
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        points.free(temp);
        return chunk;
    }

    @ThreadSafe
    public Chunk getChunkFromCoord(Point point, HashedPoint temp) {
        if (point.x < 0) temp.x = (point.x + 1) / getChunkSize() - 1;
        else temp.x = point.x / getChunkSize();
        if (point.y < 0) temp.y = (point.y + 1) / getChunkSize() - 1;
        else temp.y = point.y / getChunkSize();
        temp.recalculateHash();
        return chunks.get(temp);
    }

    public boolean removeChunk(int chunkX, int chunkY) {
        HashedPoint temp = points.obtain();
        temp.set(chunkX, chunkY);
        temp.recalculateHash();
        boolean success = removeChunk(temp);
        points.free(temp);
        return success;
    }

    @ThreadSafe
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

    public Tile getTile(int x, int y) {
        Chunk chunk = getChunkFromCoord(x, y);
        if (chunk == null) return null;
        return chunk.getTileFromWorldCoords(x, y);
    }

    public TileGamePiece getTileGamePiece(int x, int y) {
        Chunk chunk = getChunkFromCoord(x, y);
        if (chunk == null) {
            return null;
        }
        return (TileGamePiece) getGamePiece(chunk.getTileEntityFromWorldCoords(x, y));
    }

    public boolean isWalkable(int x, int y) {
        Chunk chunk = getChunkFromCoord(x, y);
        if (chunk == null) return false;
        return chunk.isWalkableFromWorldCoords(x, y);
    }

    public boolean isWalkable(Point point, HashedPoint temp) {
        Chunk chunk = getChunkFromCoord(point, temp);
        if (chunk == null) return false;
        return chunk.isWalkableFromWorldCoords(point.x, point.y);
    }

    public Chunk addChunk(Chunk chunk) {
        if (chunk == null) return null;
        chunks.put(chunk.chunkPosition, chunk);
        chunk.field = this;
        if (isServer()) {
            for (GamePiece gamePiece : chunk.itemsToAdd) {
                addGamePiece(gamePiece);
            }
        }
        for (GamePiece gamePiece : chunk.items) {
            addGamePiece(gamePiece, gamePiece.getGameId());
        }

        if (isServer()) {
            int chunkDx = chunk.getChunkX() * chunk.getChunkSize();
            int chunkDy = chunk.getChunkY() * chunk.getChunkSize();
            for (int x = 0; x < chunk.getChunkSize(); x++) {
                for (int y = 0; y < chunk.getChunkSize(); y++) {
                    Tile tile = chunk.getTile(x, y);
                    if (tile != null && tile.isSolid) {
                        int tileX = x + chunkDx;
                        int tileY = y + chunkDy;
                        addGamePiece(new SolidTile(tileX, tileY));
                    }
                }
            }
        }
        return chunk;
    }

    @ThreadSafe
    public boolean removeChunk(Point chunk) {
        Chunk c = chunks.remove(chunk);
        if (c == null) return false;
        for (GamePiece gamePiece : c.items) {
            removeGamePiece(gamePiece);
        }
        return true;
    }

    public Chunk relocateGamePiece(GamePiece item, Chunk original) {
        HashedPoint temp = points.obtain();
        Chunk chunk = relocateGamePiece(item, original, temp);
        points.free(temp);
        return chunk;
    }

    @ThreadSafe
    public Chunk relocateGamePiece(GamePiece item, Chunk original, HashedPoint temp) {
        getChunkPosition(item.getX(), item.getY(), temp);
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        if (chunk == null) {
            if (isServer() && item.shouldLoadChunk()) {
                addChunk(temp.x, temp.y);
            } else {
                return null;
            }
        }
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
        //convert delta into millis and add it to time
        time += delta * 1000;



        for (GamePiece gamePiece : getItems()) {
            try {
                gamePiece.tick(delta);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            if (!chunk.bounds.contains(gamePiece.getX(), gamePiece.getY())) {
                Chunk newChunk = relocateGamePiece(gamePiece, chunk);
                if (newChunk != chunk) {
                    iterator.remove();
                    if (newChunk != null) newChunk.add(gamePiece);
                    else {
                        remove0(gamePiece);
                    }
                }
            }
        }
    }


    public Collection<GamePiece> getItems() {
        return gamePieces.values();
    }

    public boolean isServer() {
        return true;
    }

}
