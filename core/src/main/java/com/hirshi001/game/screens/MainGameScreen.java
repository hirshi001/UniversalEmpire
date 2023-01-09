package com.hirshi001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.hirshi001.game.ClientField;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.render.ActorMap;
import com.hirshi001.game.render.FieldRender;
import com.hirshi001.game.render.tilerenderers.TileRenderers;
import com.hirshi001.game.shared.entities.GamePieces;
import com.hirshi001.game.shared.entities.Player;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.packets.*;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.networking.network.channel.Channel;
import com.hirshi001.networking.packet.DataPacket;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;

public class MainGameScreen extends GameScreen {

    FieldRender fieldRender;
    InputMultiplexer multiplexer;
    Vector2 playerAngle = new Vector2();

    public MainGameScreen(GameApp gameApp) {
        super(gameApp);
        Gdx.app.log("MainGameScreen", "Created");

        Tiles.register();
        TileRenderers.register(app);

        GamePieces.register();
        ActorMap.register();

        ClientField field;

        field = new ClientField(app.client, GameSettings.CELL_SIZE, GameSettings.CHUNK_SIZE);
        app.field = field;

        fieldRender = new FieldRender(field);



        app.client.getChannel().sendTCPWithResponse(new JoinGamePacket(), null, 10000)
                .onFailure((cause) -> {
                    Gdx.app.postRunnable(() -> {
                        Gdx.app.log("MainGameScreen", "Failed to join game");
                        app.setScreen(new ErrorScreen(app, cause));
                    });
                }).perform();

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(fieldRender);
        multiplexer.addProcessor(new InputAdapter() {
            boolean movingUp = false;
            boolean movingDown = false;
            boolean movingLeft = false;
            boolean movingRight = false;

            @Override
            public boolean keyDown(int keycode) {
                boolean modified = false;
                if (keycode == Input.Keys.W) {
                    movingUp = true;
                    modified = true;
                }
                if (keycode == Input.Keys.S) {
                    movingDown = true;
                    modified = true;
                }
                if (keycode == Input.Keys.A) {
                    movingLeft = true;
                    modified = true;
                }
                if (keycode == Input.Keys.D) {
                    movingRight = true;
                    modified = true;
                }
                if (modified) {
                    updatePlayerAngle();
                }
                return modified;
            }

            @Override
            public boolean keyUp(int keycode) {
                boolean modified = false;
                if (keycode == Input.Keys.W) {
                    movingUp = false;
                    modified = true;
                }
                if (keycode == Input.Keys.S) {
                    movingDown = false;
                    modified = true;
                }
                if (keycode == Input.Keys.A) {
                    movingLeft = false;
                    modified = true;
                }
                if (keycode == Input.Keys.D) {
                    movingRight = false;
                    modified = true;
                }
                if (modified) {
                    updatePlayerAngle();
                }
                return modified;
            }

            private void updatePlayerAngle() {
                playerAngle.setZero();
                if (movingUp) playerAngle.y += 1;
                if (movingDown) playerAngle.y -= 1;
                if (movingLeft) playerAngle.x -= 1;
                if (movingRight) playerAngle.x += 1;
            }

        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {

        if(!playerAngle.isZero()){
            playerAngle.nor();
            Player player = app.field.getPlayer();
            if(player!=null){
                Number speed = player.getProperties().get("speed", 3F);
                float d = speed.floatValue()*delta;
                player.bounds.x += playerAngle.x * d;
                player.bounds.y += playerAngle.y * d;
                player.update();

                PlayerMovePacket packet = new PlayerMovePacket(player.bounds.x, player.bounds.y, player.getGameId(), app.field.tick);
                Channel channel = GameApp.Game().client.getChannel();
                channel.sendTCP(packet, null).perform();
                if(channel.supportsUDP()) channel.sendUDP(packet, null).perform();
            }
        }


        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector3 worldCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            fieldRender.camera.unproject(worldCoords);
            Player player = app.field.getPlayer();
            if (player != null) {
                float angle = (float) Math.atan2(worldCoords.y - player.getCenterY(), worldCoords.x - player.getCenterX());
                app.client.getChannel().sendTCP(new ShootPacket(angle), null).perform();
            }
        }



        if (app.field != null) app.field.tick(delta);
        // fieldRender.getUserInput(delta);
        fieldRender.render(delta);
        // fieldRender.debugRender();

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
