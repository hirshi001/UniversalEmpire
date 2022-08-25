package com.hirshi001.game.shared.util;

import com.badlogic.gdx.math.Vector2;

public class Point {

    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public boolean equals(Object other) {
        if (other instanceof Point) {
            Point p = (Point) other;
            return p.x == x && p.y == y;
        }
        return false;
    }

    public int hashCode() {
        return Hash.elegantSigned(getX(), getY());
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public void add(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void add(Point p) {
        this.x += p.x;
        this.y += p.y;
    }

    public void sub(int x, int y) {
        this.x -= x;
        this.y -= y;
    }

    public void sub(Point p) {
        this.x -= p.x;
        this.y -= p.y;
    }

    public float dst(Vector2 v) {
        return v.dst(x, y);
    }

    public float dst(Point p) {
        return (float)Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
    }
}
