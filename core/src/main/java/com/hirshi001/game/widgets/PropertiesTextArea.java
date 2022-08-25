package com.hirshi001.game.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.hirshi001.game.GameApp;

import static com.hirshi001.game.Util.*;

public class PropertiesTextArea extends TextArea {
    public PropertiesTextArea(String text) {
        super(text, getNewStyle());
    }

    public static TextFieldStyle getNewStyle(){
        BitmapFont font = GameApp.Game().gameResources.get(FONT);
        font.getData().setScale(0.5F);
        font.setUseIntegerPositions(false);
        return new TextFieldStyle(font, Color.BLACK, new BaseDrawable(), new BaseDrawable(), new BaseDrawable());
    }


}
