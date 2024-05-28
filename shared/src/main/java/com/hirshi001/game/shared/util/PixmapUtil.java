package com.hirshi001.game.shared.util;

import com.badlogic.gdx.graphics.Pixmap;

public class PixmapUtil {

    public static byte[] getBytes(Pixmap pixmap, int x, int y, int width, int height) {
        byte[] bytes = new byte[width * height * 4];
        int i = 0;
        for (int yy = y; yy < y + height; yy++) {
            for (int xx = x; xx < x + width; xx++) {
                int color = pixmap.getPixel(xx, yy);
                bytes[i++] = (byte) ((color & 0xff000000) >> 24);
                bytes[i++] = (byte) ((color & 0x00ff0000) >> 16);
                bytes[i++] = (byte) ((color & 0x0000ff00) >> 8);
                bytes[i++] = (byte) (color & 0x000000ff);
            }
        }
        return bytes;
    }

    public static void setBytes(Pixmap pixmap, byte[] bytes, int x, int y, int width, int height) {
        int i = 0;
        for (int yy = y; yy < y + height; yy++) {
            for (int xx = x; xx < x + width; xx++) {
                int color = ((bytes[i++] & 0xff) << 24) | ((bytes[i++] & 0xff) << 16) | ((bytes[i++] & 0xff) << 8) | (bytes[i++] & 0xff);
                pixmap.drawPixel(xx, yy, color);
            }
        }
    }

}
