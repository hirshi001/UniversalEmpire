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

public class TroopSelectedGui extends Table {

    Array<Troop> selectedTroops;

    TextButton createGroupButton, addToGroupButton;

    GameGUI displayGUI = new GameGUI("", GameApp.guiSkin, Align.topLeft, () -> {
        GameApp.removeGameGui(this);
        GameApp.fieldRenderer.selectedItems.clear();
    });

    Table troopTable = new Table();
    ScrollPane troopScroll = new ScrollPane(troopTable, GameApp.guiSkin);


    public TroopSelectedGui() {
        super();
        selectedTroops = new Array<>();

        createGroupButton = new TextButton("Create Group", GameApp.guiSkin);
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
                                Array<Integer> troopIds = new Array<>();
                                for (Troop troop : selectedTroops) {
                                    troopIds.add(troop.getGameId());
                                }
                                GameApp.field.createTroopGroup(GameApp.field.getControllerId(), input, troopIds);

                                Gdx.app.postRunnable(() -> GameApp.removeGameGui(gui));
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
        displayGUI.add(createGroupButton).expandX().row();

        addToGroupButton = new TextButton("Add to Group", GameApp.guiSkin);
        addToGroupButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Table table = new Table();
                        table.setFillParent(true);
                        table.center();

                        Window window = new GameGUI("", GameApp.guiSkin, Align.topLeft, () -> GameApp.removeGameGui(table));
                        window.setModal(true);
                        window.setMovable(false);
                        window.setResizable(false);

                        window.add(new Label("Select Group To Add To", GameApp.guiSkin)).center().uniformX().row();
                        for(TroopGroup group:GameApp.field.playerData.troopGroups.values()){
                            TextButton button = new TextButton(group.name, GameApp.guiSkin);
                            button.addListener(new ClickListener(){
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);
                                    Array<Integer> troopIds = new Array<>();
                                    for (Troop troop : selectedTroops) {
                                        troopIds.add(troop.getGameId());
                                    }
                                    GameApp.field.addTroopsToGroup(GameApp.field.getControllerId(), group.name, troopIds);
                                    Gdx.app.postRunnable( ()->GameApp.removeGameGui(table));
                                }
                            });
                            window.add(button).center().uniformX().row();
                        }

                        table.add(window).center().row();
                        GameApp.addGameGui(table);
                    }
                });
            }
        });

        displayGUI.add(addToGroupButton).expandX().row();

        troopScroll.setScrollbarsOnTop(false);
        troopScroll.setFadeScrollBars(false);
        displayGUI.add(troopScroll).maxHeight(150).growX().row();

        bottom().right();
        add(displayGUI).bottom().right();

    }

    public void clearSelection() {
        selectedTroops.clear();
        troopTable.reset();
    }

    public void addTroop(Troop troop) {
        selectedTroops.add(troop);
        Label label = new Label(troop.getClass().getSimpleName() + " - Id: " + troop.getGameId(), GameApp.guiSkin);
        troopTable.add(label).row();

    }
}
