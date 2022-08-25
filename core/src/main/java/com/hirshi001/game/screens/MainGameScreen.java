package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.GestureDetector;
import com.hirshi001.game.ClientField;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.render.ActorMap;
import com.hirshi001.game.render.FieldRender;
import com.hirshi001.game.render.tilerenderers.TileRenderers;
import com.hirshi001.game.shared.entities.GamePieces;
import com.hirshi001.game.shared.entities.Player;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.packets.GameInitPacket;
import com.hirshi001.game.shared.packets.JoinGamePacket;
import com.hirshi001.game.shared.packets.SyncPacket;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;

public class MainGameScreen extends GameScreen {

    FieldRender fieldRender;

    public MainGameScreen(GameApp gameApp) {
        super(gameApp);

        Tiles.register();
        TileRenderers.register(app);

        GamePieces.register();
        ActorMap.register();

        ClientField field;

        field = new ClientField(app.client, GameSettings.CELL_SIZE, GameSettings.CHUNK_SIZE);
        app.field = field;

        fieldRender = new FieldRender(field);

        app.client.sendTCPWithResponse(new JoinGamePacket(), null, 1000).map(ctx -> (PacketHandlerContext<GameInitPacket>) ctx)
                        .then((ctx)->{
                        }).onFailure((cause)->{
                            Gdx.app.postRunnable( ()-> app.setScreen(new ErrorScreen(app, cause)));
                }).perform();


        Gdx.input.setInputProcessor(fieldRender);
    }

    @Override
    public void render(float delta) {

        if(app.field!=null) app.field.tick(delta);
        fieldRender.getUserInput(delta);
        fieldRender.render(delta);
        fieldRender.debugRender();

    }


    @Override
    public void resize(int width, int height) {
        fieldRender.update(width, height);
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
