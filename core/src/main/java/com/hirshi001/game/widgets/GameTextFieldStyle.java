package com.hirshi001.game.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameTextFieldStyle extends TextField.TextFieldStyle {


    public GameTextFieldStyle(int width, int height, int borderWidth, TextureRegion border, TextureRegion background, BitmapFont font) {
        super();

        Pixmap borderPixmap = BorderTexture.createBorderTexture(border, width, height, borderWidth);
        Pixmap backgroundPixmap = BorderTexture.createRepeatableRectangle(background, width, height);

        //combine the pixmaps
        backgroundPixmap.setBlending(Pixmap.Blending.SourceOver);
        for (int row = 0; row < borderPixmap.getHeight(); row++) {
            for (int col = 0; col < borderPixmap.getWidth(); col++) {
                backgroundPixmap.drawPixel(col, row, borderPixmap.getPixel(col, row));
            }
        }
        borderPixmap.dispose();

        TextureRegion back = new TextureRegion(new Texture(backgroundPixmap));

        this.font = font;
        font.setUseIntegerPositions(false);
        font.getData().scale(0.1F);
        this.font.setUseIntegerPositions(false);
        this.fontColor = Color.BLACK;

        this.background = new TextureRegionDrawable(back);

        Pixmap cp = new Pixmap(2, height, Pixmap.Format.RGBA8888);
        cp.setColor(fontColor);
        cp.fill();

        this.cursor = new TextureRegionDrawable(new Texture(cp));

    }
}
