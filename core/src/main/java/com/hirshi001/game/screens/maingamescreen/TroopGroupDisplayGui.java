package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.util.stringutils.StringUtils;
import com.hirshi001.game.widgets.Styles;

import java.util.*;

public class TroopGroupDisplayGui extends Table {

    List<TroopGroup> displayTroopGroups;
    Set<TroopGroup> displaySet;
    Collection<TroopGroup> troopGroups;
    GameGUI displayGui;

    TextureRegion arrowRegion;
    TroopGroupInfoGUI troopGroupInfoGUI = new TroopGroupInfoGUI();
    boolean open = true;

    public TroopGroupDisplayGui(Collection<TroopGroup> troopGroups) {
        super();
        this.troopGroups = troopGroups;
        this.displayTroopGroups = new LinkedList<>(troopGroups);
        this.displaySet = new HashSet<>(troopGroups);
        this.displayGui = new GameGUI();
        redo();

        arrowRegion = GameApp.gameResources.getFromAtlas("gui/UpArrow");
        TextureRegionDrawable arrow = new TextureRegionDrawable(arrowRegion);

        TextureRegion region = GameApp.gameResources.getFromAtlas("gui/Gui");
        NinePatch ninePatch = new NinePatch(region, 2, 2, 2, 2);

        Drawable drawable1 = new NinePatchDrawable(ninePatch) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                super.draw(batch, x, y, width, height);
                arrow.draw(batch, x, y, width, height);
            }
        };
        Button button = new Button(drawable1, drawable1, drawable1);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setOpen(!open, true);
            }
        });

        top();

        add(displayGui).top().expandX().fillX();
        row();
        add(button).center();
        pack();


    }

    public void setOpen(boolean open, boolean animate) {
        if(getActions().size!=0) getActions().clear();
        if (open) {
            if (animate) addAction(Actions.moveTo(0F, 0F, 0.5F));
            else setY(0);
        } else {
            float y = displayGui.getHeight()*0.9F;
            if (animate) addAction(Actions.moveTo(0F, y, 0.5F));
            else setY(y);
        }
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (displaySet.containsAll(troopGroups)) return;
        displayTroopGroups.removeIf(troopGroup -> !troopGroups.contains(troopGroup));
        troopGroups.forEach((g) -> {
            if (!displaySet.contains(g)) {
                displayTroopGroups.add(g);
                displaySet.add(g);
            }
        });
        displaySet.retainAll(troopGroups);
        redo();
        pack();
    }

    public void redo() {
        displayGui.reset();
        if (displayTroopGroups.isEmpty()) {
            Label label = new Label("No groups have been created", Styles.labelStyle);
            displayGui.add(label).expandX().padLeft(10F).left();
        }
        for (TroopGroup troopGroup : displayTroopGroups) {
            Button button = new TextButton(StringUtils.DEFAULT().toString(troopGroup), Styles.textButtonStyle);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    troopGroupInfoGUI.setTroopGroup(troopGroup);
                    troopGroupInfoGUI.setVisible(true);

                    GameApp.removeGameGui(troopGroupInfoGUI);

                    GameApp.addGameGui(troopGroupInfoGUI);

                    troopGroupInfoGUI.setFillParent(true);
                    troopGroupInfoGUI.setVisible(true);
                    troopGroupInfoGUI.center().right();
                }
            });
            displayGui.add(button).padLeft(10F);
        }
        displayGui.pack();
    }
}
