package com.hirshi001.game.shared.util;

import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.IntPoint;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.SearchNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

public class PathFinder {


    private static final Pool<SearchNode> defaultPool = new Pool<SearchNode>() {
        @Override
        protected SearchNode newObject() {
            return new SearchNode();
        }
    };

    private static final int[] dx = new int[]{0, 1, 0, -1};
    private static final int[] dy = new int[]{1, 0, -1, 0};


    public static LinkedList<IntPoint> findPathList(Field field, int startX, int startY, int endX, int endY, Pool<SearchNode> pool) {
        SearchNode node = findPath(field, startX, startY, endX, endY, pool);
        if (node == null) {
            return null;
        }
        LinkedList<IntPoint> points = new LinkedList<>();
        while (node != null) {
            points.addFirst(new IntPoint(node.x, node.y));
            node = node.predecessor;
        }
        return points;//smoothPath(field, points);
    }

    public static SearchNode findPath(Field field, int startX, int startY, int endX, int endY, Pool<SearchNode> pool) {
        return findPath(field, startX, startY, endX, endY, 256, pool);
    }

    // TODO: Change from dijkstra to A*
    @SuppressWarnings("all")
    public static SearchNode findPath(Field field, int startX, int startY, int endX, int endY, int maxSteps, Pool<SearchNode> pool) {
        if (pool == null) pool = defaultPool;
        if (Math.abs(startX - endX) + Math.abs(startY - endY) > maxSteps) {
            return null;
        }
        if (startX == endX && startY == endY) {
            return new SearchNode(startX, startY, 0, null);
        }

        HashedPoint point = new HashedPoint(), tempPoint = new HashedPoint();
        point.set(endX, endY);
        point.recalculateHash();
        if (!field.isWalkable(point, tempPoint)) {
            return null;
        }

        PriorityQueue<SearchNode> points = new PriorityQueue<>();
        Map<SearchNode, SearchNode> visitedNodes = new HashMap<>();

        SearchNode startNode = pool.obtain();
        startNode.set(startX, startY, 0, null);

        points.add(startNode);
        visitedNodes.put(startNode, startNode);

        int x, y, i;
        SearchNode node, temp = pool.obtain(), nextNode;

        finish:
        while (true) {
            node = points.poll();
            for (i = 0; i < 4; i++) {
                x = node.x + dx[i];
                y = node.y + dy[i];
                point.set(x, y);
                point.recalculateHash();
                if (!field.isWalkable(point, tempPoint)) continue;


                // calculate newCost using manhattan distance and make it for A*
                // int newCost = node.cost + Math.abs(x - endX) + Math.abs(y - endY);
                int newCost = node.cost + 1; // (for dijkstra)

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
                    nextNode = pool.obtain();
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
            pool.free(n);
        }
        pool.free(temp);

        if (node.x == endX && node.y == endY) {
            return node;
        }
        return null;

    }


    public static LinkedList<IntPoint> smoothPath(Field field, LinkedList<IntPoint> path) {
        LinkedList<IntPoint> smoothedPath = new LinkedList<>();
        smoothedPath.add(path.getFirst());

        for (int i = 2; i < path.size(); i++) {
            IntPoint prev = smoothedPath.getLast();
            IntPoint current = path.get(i);

            if (!field.isWalkable(prev.x, prev.y) || !field.isWalkable(current.x, current.y)) {
                smoothedPath.add(path.get(i - 1));
            }
        }

        smoothedPath.add(path.getLast());

        return smoothedPath;
    }


}
