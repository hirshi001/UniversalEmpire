package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.widgets.Styles;

public class GameGUI extends Window {

    private static final int PIXEL_PAD = 5;

    boolean includeQuitButton = false;
    int align = Align.left;
    Runnable onQuit;

    public GameGUI(String title, Skin skin) {
        super(title, skin);
    }


    public GameGUI(String title, Skin skin, int quitButtonAlign, Runnable onQuit) {
        super(title, skin);
        this.includeQuitButton = true;
        if(Align.isLeft(quitButtonAlign)){
            align = Align.topLeft;
        }else{
            align = Align.topRight;
        }
        this.onQuit = onQuit;
        setClip(false);
        addQuitButton();

    }

    public void reset() {
        clear();
        if(includeQuitButton) addQuitButton();
    }

    public void addQuitButton() {
        ImageButton quitButton = new ImageButton(Styles.quitButtonStyle);
        quitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(onQuit!=null) onQuit.run();
            }
        });

        float size = 20F;
        float pad = -size/2 - PIXEL_PAD;

        Cell<ImageButton> cell = add(quitButton);
        cell.align(align);
        cell.size(size, size);


        cell.padTop(pad);
        if(Align.isLeft(align)){
            cell.padLeft(pad);
        }else{
            cell.padRight(pad);
        }

        cell.row();
    }

}
