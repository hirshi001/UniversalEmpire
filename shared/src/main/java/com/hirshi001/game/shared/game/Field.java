package com.hirshi001.game.shared.game;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.dongbat.jbump.IntPoint;
import com.dongbat.jbump.World;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.SolidTile;
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
    public long time;
    private final Map<Integer, GamePiece> gamePieces;
    private final Array<GamePiece> gamePiecesToAdd = new Array<>(), addedGamePieces = new Array<>(),
            gamePiecesToRemove = new Array<>(), removedGamePieces = new Array<>();
    private final Pool<HashedPoint> points;
    private final Pool<SearchNode> searchNodes;

    private final int[] dx = new int[]{0, 1, 0, -1};
    private final int[] dy = new int[]{1, 0, -1, 0};


    public Field(float cellSize, int chunkSize) {
        super(cellSize);
        this.chunkSize = chunkSize;
        gamePieces = new HashMap<>();
        points = new Pool<HashedPoint>() {
            @Override
            protected HashedPoint newObject() {
                return new HashedPoint();
            }
        };

        searchNodes = new Pool<SearchNode>(chunkSize * chunkSize) {
            @Override
            protected SearchNode newObject() {
                return new SearchNode();
            }
        };

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

    public LinkedList<IntPoint> findPathList(int startX, int startY, int endX, int endY) {
        SearchNode node = findPath(startX, startY, endX, endY);
        if (node == null) {
            return null;
        }
        LinkedList<IntPoint> points = new LinkedList<>();
        while (node != null) {
            points.addFirst(new IntPoint(node.x, node.y));
            node = node.predecessor;
        }
        return points;
    }

    public SearchNode findPath(int startX, int startY, int endX, int endY) {
        return findPath(startX, startY, endX, endY, 256);
    }

    // TODO: Change from dijkstra to A*
    @SuppressWarnings("all")
    public SearchNode findPath(int startX, int startY, int endX, int endY, int maxSteps) {
        if (Math.abs(startX - endX) + Math.abs(startY - endY) > maxSteps) {
            return null;
        }
        if (startX == endX && startY == endY) {
            return new SearchNode(startX, startY, 0, null);
        }


        Tile tile = getTile(endX, endY);
        if (tile == null || tile.isSolid) {
            return null;
        }

        tile = getTile(startX, startY);
        if (tile == null || tile.isSolid) {
            return null;
        }


        PriorityQueue<SearchNode> points = new PriorityQueue<>();
        Map<SearchNode, SearchNode> visitedNodes = new HashMap<>();

        SearchNode startNode = searchNodes.obtain();
        startNode.set(startX, startY, 0, null);

        points.add(startNode);
        visitedNodes.put(startNode, startNode);

        int x, y, i;
        SearchNode node, temp = searchNodes.obtain(), nextNode;

        finish:
        while (true) {
            node = points.poll();
            for (i = 0; i < 4; i++) {
                x = node.x + dx[i];
                y = node.y + dy[i];
                Tile t = getTile(x, y);
                if (t == null || t.isSolid) continue;


                // calculate newCost using manhattan distance and make it for A*
                int newCost = node.cost + Math.abs(x - endX) + Math.abs(y - endY);
                // int newCost = node.cost + 1; // (for dijkstra)

                // if (newCost > maxSteps) continue;

                nextNode = visitedNodes.get(temp.set(x, y, 0, null));
                if (nextNode != null) {
                    if (nextNode.cost > newCost) {
                        nextNode.cost = newCost;
                        nextNode.predecessor = node;
                        points.remove(nextNode);
                        points.add(nextNode);
                    }
                } else {
                    nextNode = searchNodes.obtain();
                    nextNode.predecessor = node;
                    nextNode.set(x, y, newCost, node);
                    points.add(nextNode);
                    visitedNodes.put(nextNode, nextNode);
                }
                if (x == endX && y == endY) {
                    node = nextNode;
                    break finish;
                }
            }
            if (points.isEmpty()) {
                return null;
            }
        }

        // free all search nodes before returning
        for (SearchNode n : visitedNodes.keySet()) {
            searchNodes.free(n);
        }
        searchNodes.free(temp);

        if (node.x == endX && node.y == endY) {
            return node;
        }
        return null;

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
            gamePiece.removed();
        } catch (Exception ignored) {
        } //catch and ignore if a game piece is already removed
        if (gamePiece.chunk != null) gamePiece.chunk.remove(gamePiece);
        return gamePieces.remove(gamePiece.getGameId()) != null;
    }

    protected void add0(GamePiece gamePiece, int i) {
        HashedPoint temp = points.obtain();
        gamePieces.put(i, gamePiece);
        gamePiece.setGameId(i);
        temp.set(MathUtils.floor(gamePiece.getCenterX() / getChunkSize()), MathUtils.floor(gamePiece.getCenterY() / getChunkSize()));
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        if (chunk == null) {
            return;
        }
        chunk.add(gamePiece); // it's okay if the game piece is already in the chunk

        if (gamePiece.worldInteractable()) {
            add(gamePiece, gamePiece.bounds.x, gamePiece.bounds.y, gamePiece.bounds.width, gamePiece.bounds.height);
        }
        gamePiece.setField(this);
        points.free(temp);
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
        return addChunk(loadChunk(chunkX, chunkY));
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

    public boolean removeChunk(int chunkX, int chunkY) {
        HashedPoint temp = points.obtain();
        temp.set(chunkX, chunkY);
        temp.recalculateHash();
        boolean success = removeChunk(temp);
        points.free(temp);
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

    public Tile getTile(int x, int y) {
        Chunk chunk = getChunkFromCoord(x, y);
        if (chunk == null) return null;
        return chunk.getTileFromWorldCoords(x, y);
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
        HashedPoint temp = points.obtain();
        temp.set(x - chunk.getChunkX() * chunk.getChunkSize(), y - chunk.getChunkY() * chunk.getChunkSize());
        temp.recalculateHash();
        GamePiece piece = chunk.tileGamePieces.get(temp);
        points.free(temp);
        return piece;
    }

    public Chunk addChunk(Chunk chunk) {
        if (chunk == null) return null;
        chunks.put(chunk.chunkPosition, chunk);
        chunk.field = this;
        if (isServer()) {
            for(GamePiece gamePiece : chunk.itemsToAdd) {
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
        getChunkPosition(item.getCenterX(), item.getCenterY(), temp);
        temp.recalculateHash();
        Chunk chunk = chunks.get(temp);
        if (chunk == null) {
            if (isServer() && item.shouldLoadChunk()) {
                addChunk(temp.x, temp.y);
            } else {
                removeGamePiece(item);
                return null;
            }
        }
        points.free(temp);
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
                Chunk newChunk = relocateGamePiece(gamePiece, chunk);
                if (newChunk != chunk) {
                    // chunk.remove(gamePiece);
                    iterator.remove();
                    if (newChunk != null) newChunk.add(gamePiece);
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
