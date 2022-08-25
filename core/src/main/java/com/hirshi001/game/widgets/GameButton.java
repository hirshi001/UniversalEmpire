package com.hirshi001.game.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hirshi001.game.GameApp;

import static com.hirshi001.game.Util.*;

public class GameButton extends TextButton {


    public GameButton(int pixelWidth, int pixelHeight, int borderWidth, String text) {
        super(text, getStyle(pixelWidth, pixelHeight, borderWidth));
    }

    private static GameButtonStyle getStyle(int pixelWidth, int pixelHeight, int borderWidth){
        GameApp app = GameApp.Game();
        TextureRegion border = new TextureRegion((Texture) app.gameResources.get(BORDER_TEXTURE));
        TextureRegion background = new TextureRegion((Texture) app.gameResources.get(BACKGROUND_TEXTURE));
        BitmapFont font = app.gameResources.get(FONT);
        return new GameButtonStyle(pixelWidth, pixelHeight, borderWidth, border, background, font);
    }

}

class GameButtonStyle extends TextButton.TextButtonStyle {


    public GameButtonStyle(int width, int height, int borderWidth, TextureRegion border, TextureRegion background, BitmapFont font) {
        super();

        Pixmap borderPixmap = BorderTexture.createBorderTexture(border, width, height, borderWidth);
        Pixmap backgroundPixmap = BorderTexture.createRepeatableRectangle(background, width, height);

        //combine the pixmaps
        backgroundPixmap.setBlending(Pixmap.Blending.SourceOver);
        for(int row = 0; row < borderPixmap.getHeight(); row++){
            for(int col = 0; col < borderPixmap.getWidth(); col++){
                backgroundPixmap.drawPixel(col, row, borderPixmap.getPixel(col, row));
            }
        }
        borderPixmap.dispose();

        TextureRegion up = new TextureRegion(new Texture(backgroundPixmap));


        Pixmap downPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        downPixmap.setBlending(Pixmap.Blending.SourceOver);
        downPixmap.drawPixmap(backgroundPixmap, 0, 0, 0, 0, width, height);

        downPixmap.setColor(new Color(0F, 0F, 1F, 0.25F));
        downPixmap.fillRectangle(0, 0, width, height);



        TextureRegion down = new TextureRegion(new Texture(downPixmap));


        this.font = font;
        this.font.setUseIntegerPositions(false);
        this.fontColor = Color.BLACK;

        this.up = new TextureRegionDrawable(up);
        this.down = new TextureRegionDrawable(down);

    }



}

