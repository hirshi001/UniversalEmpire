package com.hirshi001.game.shared.game;

import org.jetbrains.annotations.NotNull;

public class SearchNode implements Comparable<SearchNode>{

    public int x, y, cost;
    public SearchNode predecessor;

    public SearchNode(){

    }

    public SearchNode(int x, int y, int cost, SearchNode predecessor){
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.predecessor = predecessor;
    }

    public SearchNode set(int x, int y, int cost, SearchNode predecessor){
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.predecessor = predecessor;
        return this;
    }

    @Override
    public int compareTo(@NotNull SearchNode o) {
        return Integer.compare(cost, o.cost);
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SearchNode && ((SearchNode) obj).x == x && ((SearchNode) obj).y == y;
    }
}
