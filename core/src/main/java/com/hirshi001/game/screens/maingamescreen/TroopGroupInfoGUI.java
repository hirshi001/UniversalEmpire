package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.widgets.Styles;

public class TroopGroupInfoGUI extends Table {

    private TroopGroup troopGroup;
    Label label = new Label("", GameApp.guiSkin);
    GameGUI gameGUI = new GameGUI("", GameApp.guiSkin, Align.topLeft, () -> GameApp.removeGameGui(this));

    Table displayTroopWindow = new Table();
    ScrollPane scrollPane = new ScrollPane(displayTroopWindow, GameApp.guiSkin);

    public TroopGroupInfoGUI() {
        super();
        setFillParent(true);
        center();
        right();
        add(gameGUI).center().right();

        label.setAlignment(Align.center);
        gameGUI.add(label).center().padBottom(10F).row();

        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsOnTop(true);
        gameGUI.add(scrollPane).maxHeight(300F).center().row();
    }

    public void setTroopGroup(TroopGroup troopGroup) {
        this.troopGroup = troopGroup;
        displayTroopWindow.clear();
        label.setText(troopGroup.name);
        label.setAlignment(Align.center);

        //noinspection GDXJavaUnsafeIterator
        for (Troop troop : troopGroup.troops) {
            String troopInfo = troop.getClass().getSimpleName() + " " + troop.getTroopTier() + " (id=" + troop.getGameId() + ")";
            displayTroopWindow.add(new Label(troopInfo, GameApp.guiSkin)).center().row();
        }

    }


}
