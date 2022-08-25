package com.hirshi001.game.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hirshi001.game.GameApp;

import static com.hirshi001.game.Util.*;

public class GameTextField extends TextField {



    public GameTextField(int pixelWidth, int pixelHeight, int borderWidth, String text) {
        super(text, getStyle(pixelWidth, pixelHeight, borderWidth));
    }

    private static TextFieldStyle getStyle(int pixelWidth, int pixelHeight, int borderWidth){
        TextureRegion border = new TextureRegion((Texture) GameApp.Game().gameResources.get(BORDER_TEXTURE));
        TextureRegion background = new TextureRegion((Texture) GameApp.Game().gameResources.get(BACKGROUND_TEXTURE));
        BitmapFont font = GameApp.Game().gameResources.get(FONT);
        return new GameTextFieldStyle(pixelWidth, pixelHeight, borderWidth, border, background, font);
    }
}


