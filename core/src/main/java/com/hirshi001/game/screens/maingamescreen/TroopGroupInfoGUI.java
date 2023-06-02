package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.widgets.Styles;

public class TroopGroupInfoGUI extends Table {

    private TroopGroup troopGroup;
    Label label = new Label("", Styles.labelStyle);
    GameGUI gameGUI = new GameGUI();

    public TroopGroupInfoGUI() {
        super();
        setFillParent(true);
        center();
        right();
        add(gameGUI).center().right();
    }

    public void setTroopGroup(TroopGroup troopGroup) {
        this.troopGroup = troopGroup;
        gameGUI.clear();
        label = new Label(troopGroup.name, Styles.labelStyle);
        gameGUI.add(label).center().row();
        for(Troop troop:troopGroup.troops){
            String troopInfo = troop.getClass().getSimpleName() + " " + troop.getTroopTier()+ " (id=" + troop.getGameId() + ")";
            gameGUI.add(new Label(troopInfo, Styles.labelStyle)).center().row();
        }

    }


}
