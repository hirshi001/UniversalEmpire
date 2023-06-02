package com.hirshi001.game.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.hirshi001.game.GameApp;

public class Styles {

    public static TextureRegion whitePixel;

    public static TextButton.TextButtonStyle textButtonStyle;
    public static TextField.TextFieldStyle textFieldStyle;

    public static Label.LabelStyle labelStyle;
    public static ImageButton.ImageButtonStyle quitButtonStyle;


    public static void loadStyles() {
        whitePixel = GameApp.gameResources.getFromAtlas("gui/WhitePixel");


        BitmapFont font = GameApp.gameResources.get("font-16");
        NinePatch patch = new NinePatch(GameApp.gameResources.getFromAtlas("gui/Button"), 11, 11, 11, 11);
        NinePatchDrawable patchDrawable = new NinePatchDrawable(patch);
        textButtonStyle = new TextButton.TextButtonStyle(patchDrawable, patchDrawable, patchDrawable, font);



        NinePatch patch2 = new NinePatch(GameApp.gameResources.getFromAtlas("gui/TextInput"), 3, 3, 3, 3);
        NinePatchDrawable patchDrawable2 = new NinePatchDrawable(patch2);
        textFieldStyle = new TextField.TextFieldStyle(font, Color.BLACK, new BaseDrawable(){
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(Color.BLACK);
                batch.draw(whitePixel, x, y, width, height);
                batch.setColor(Color.WHITE);
            }
        },new BaseDrawable(){
            final Color highlight = new Color(1, 0, 0, 0.5f);
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.setColor(highlight);
                batch.draw(whitePixel, x, y, width, height);
                batch.setColor(Color.WHITE);
            }
        }, patchDrawable2);
        textFieldStyle.cursor.setMinWidth(2F);

        labelStyle = new Label.LabelStyle(font, Color.BLACK);
        labelStyle.background = patchDrawable2;


        TextureRegion quitTexture = GameApp.gameResources.getFromAtlas("gui/exit");
        quitButtonStyle = new ImageButton.ImageButtonStyle();
        quitButtonStyle.up = new BaseDrawable(){
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                batch.draw(quitTexture, x, y, width, height);
            }
        };

    }


}
