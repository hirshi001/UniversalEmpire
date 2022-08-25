package com.hirshi001.game.shared.util;

public class Hash {

    public static int elegant(int x, int y) {
        return x < y ? y * y + x : x * x + x + y;
    }

    public static int elegantSigned(int x, int y) {
        if (x < 0) {
            if (y < 0)
                return 3 + 4 * elegant(-x - 1, -y - 1);
            return 2 + 4 * elegant(-x - 1, y);
        }
        if (y < 0)
            return 1 + 4 * elegant(x, -y - 1);
        return 4 * elegant(x, y);
    }
}
