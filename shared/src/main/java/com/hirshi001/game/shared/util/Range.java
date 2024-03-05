package com.hirshi001.game.shared.util;

public class Range {

    public static Range largestRange() {
        return new Range(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public static Range zero() {
        return new Range(0, 0);
    }

    public float min;
    public float max;

    public Range(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public boolean contains(double value) {
        return value >= min && value <= max;
    }

    public boolean contains(Range range) {
        return range.min >= min && range.max <= max;
    }

    public boolean overlaps(Range range) {
        return range.min <= max && range.max >= min;
    }

    public double getLength() {
        return max - min;
    }


}
