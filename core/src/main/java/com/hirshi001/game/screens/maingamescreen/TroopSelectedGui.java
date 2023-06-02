package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.widgets.Styles;

public class TroopSelectedGui extends Table {

    Array<Troop> selectedTroops;
    BitmapFont font;

    TextButton createGroupButton, addToGroupButton;

    GameGUI displayGUI = new GameGUI(Align.topLeft, ()->{
        GameApp.removeGameGui(this);
        GameApp.fieldRenderer.selectedItems.clear();
    });


    public TroopSelectedGui() {
        super();
        selectedTroops = new Array<>();
        font = GameApp.gameResources.get("font-16");

        createGroupButton = new TextButton("Create Group", Styles.textButtonStyle);
        createGroupButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        InputGui inputGui = new InputGui("Group Name", new InputGui.InputListener() {
                            @Override
                            public void onInput(String input, InputGui gui) {
                                TroopGroup group = new TroopGroup(GameApp.field, input, GameApp.field.getControllerId());
                                for (Troop troop : selectedTroops) {
                                    group.addTroop(troop);
                                }
                                Gdx.app.postRunnable( ()-> {
                                    GameApp.field.addTroopGroup(group);
                                    GameApp.removeGameGui(gui);
                                });
                            }
                        });
                        inputGui.setFillParent(true);

                        GameApp.addGameGui(inputGui);
                        inputGui.invalidate();
                        inputGui.pack();
                    }
                });
            }
        });

        addToGroupButton = new TextButton("Add to Group", Styles.textButtonStyle);
        addToGroupButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        InputGui inputGui = new InputGui("Group Name", new InputGui.InputListener() {
                            @Override
                            public void onInput(String input, InputGui gui) {
                                TroopGroup group = GameApp.field.getTroopGroup(input);
                                Gdx.app.postRunnable( ()-> {
                                    GameApp.field.addTroopsToGroup(group, selectedTroops);
                                    GameApp.removeGameGui(gui);
                                });
                            }
                        });
                        inputGui.setFillParent(true);

                        GameApp.addGameGui(inputGui);
                        inputGui.invalidate();
                        inputGui.pack();
                    }
                });
            }
        });

        bottom().right();
        add(displayGUI).bottom().right();

    }

    public void clearSelection() {
        selectedTroops.clear();
        displayGUI.reset();
        displayGUI.add(createGroupButton).expandX().row();
    }

    public void addTroop(Troop troop) {
        selectedTroops.add(troop);
        Label label = new Label(troop.getClass().getSimpleName() + " - Id: " + troop.getGameId(), Styles.labelStyle);
        displayGUI.add(label).row();
    }
}
