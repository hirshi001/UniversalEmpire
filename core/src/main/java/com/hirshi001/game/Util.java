package com.hirshi001.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.dongbat.jbump.World;

public class Util {

    public static TextureRegion WHITE_TEXTURE;

    public static final String BORDER_TEXTURE = "button_border";
    public static final String BACKGROUND_TEXTURE = "button_background";
    public static final String FONT = "font-256.fnt";

    public static void loadUtil(){
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.drawPixel(0, 0, Color.WHITE.toIntBits());
        WHITE_TEXTURE = new TextureRegion(new Texture(pixmap));
    }

}
