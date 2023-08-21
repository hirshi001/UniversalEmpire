package com.hirshi001.game.screens.maingamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.hirshi001.game.ClientField;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.render.ActorMap;
import com.hirshi001.game.render.FieldRender;
import com.hirshi001.game.render.tilerenderers.TileRenderers;
import com.hirshi001.game.screens.ErrorScreen;
import com.hirshi001.game.screens.GameScreen;
import com.hirshi001.game.shared.entities.GamePieces;
import com.hirshi001.game.shared.packets.JoinGamePacket;
import com.hirshi001.game.shared.packets.PingPacket;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.game.widgets.Styles;
import com.hirshi001.networking.network.client.Client;
import com.hirshi001.networking.packethandlercontext.PacketType;
import com.hirshi001.restapi.RestAPI;

public class MainGameScreen extends GameScreen {

    public SettingsGUI settingsGui;
    public FieldRender fieldRender;
    public InputMultiplexer multiplexer;
    public Stage guiStage;

    public MainGameScreen(GameApp gameApp) {
        super(gameApp);
        Gdx.app.log("MainGameScreen", "Created");

        Tiles.register();
        TileRenderers.register();

        GamePieces.register();
        ActorMap.register();

        Styles.loadStyles();

        ClientField field;

        field = new ClientField(GameApp.client, GameSettings.CHUNK_SIZE);
        GameApp.field = field;
        fieldRender = new FieldRender(field);
        field.setFieldRender(fieldRender);
        GameApp.fieldRenderer = fieldRender;


        GameApp.client.getChannel().sendTCPWithResponse(new JoinGamePacket(), null, 10000)
                .onFailure((cause) -> {
                    Gdx.app.postRunnable(() -> {
                        Gdx.app.log("MainGameScreen", "Failed to join game");
                        app.setScreen(new ErrorScreen(app, cause));
                    });
                }).perform();

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(guiStage = new Stage(new ScreenViewport()));
        multiplexer.addProcessor(fieldRender);


        Gdx.input.setInputProcessor(multiplexer);


        TroopGroupDisplayGui troopGroupDisplayGui = new TroopGroupDisplayGui(field.playerData.troopGroups.values());
        troopGroupDisplayGui.setFillParent(true);
        addGameGUI(troopGroupDisplayGui);



        // guiStage.setDebugAll(true);


    }

    public Stage getGuiStage(){
        return guiStage;
    }

    float time;

    @Override
    public void render(float delta) {
        // send a ping packet every second

        time += delta;
        if (time > 1) {
            time = 0;
            GameApp.client.getChannel().sendWithResponse(new PingPacket(System.currentTimeMillis()), null, PacketType.TCP, 1000).then(ctx -> {
                long dt = System.currentTimeMillis() - ((PingPacket) ctx.packet).time;
                float ping = dt / 1000f;
                Gdx.app.log("MainGameScreen", "Ping: " + ping);
            }).performAsync();
        }




        {
            Client client = GameApp.client;
            if (client != null) {
                client.getChannel().flushTCP();
            }
        }

        if (GameApp.field != null) GameApp.field.tick(delta);
        // fieldRender.getUserInput(delta);
        fieldRender.render(delta);
        fieldRender.debugRender();
        {
            Client client = GameApp.client;
            if (client != null) {
                client.getChannel().flushTCP();
            }
        }

        guiStage.getViewport().apply();
        guiStage.act(delta);
        guiStage.draw();
    }

    public void addGameGUI(Actor gameGUI) {
        guiStage.addActor(gameGUI);

        guiStage.setScrollFocus(gameGUI);
        guiStage.setKeyboardFocus(gameGUI);
    }

    public boolean removeGameGUI(Actor gameGUI) {
        return gameGUI.remove();
    }


    @Override
    public void resize(int width, int height) {
        fieldRender.update(width, height);
        guiStage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
