package com.hirshi001.game.shared.util;

public final class HashedPoint extends Point{

    private int hash;

    public HashedPoint(int x, int y) {
        super(x, y);
        recalculateHash();
    }

    public HashedPoint() {
        super();
        recalculateHash();
    }

    public HashedPoint(Point p) {
        super(p);
        recalculateHash();
    }

    @Override
    public final int hashCode() {
        return hash;
    }

    public final void recalculateHash(){
        hash = super.hashCode();
    }
}
