package com.hirshi001.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.hirshi001.game.ClientField;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.render.tilerenderers.TileRenderer;
import com.hirshi001.game.screens.maingamescreen.SettingsGUI;
import com.hirshi001.game.screens.maingamescreen.TroopSelectedGui;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.util.Settings;
import com.hirshi001.game.widgets.PropertiesTextArea;

public class FieldRender extends InputAdapter {

    SpriteBatch batch;
    public OrthographicCamera camera;
    ExtendViewport viewport;
    Field field;
    ShapeRenderer renderer;

    Stage stage;
    Table table;
    public final Array<GamePiece> selectedItems = new Array<>();
    public final Array<GamePiece> writtenItems = new Array<>();
    PropertiesTextArea textArea = new PropertiesTextArea("");

    TroopSelectedGui selectionGUI;
    SettingsGUI settingsGUI;

    TileRenderer tileRenderer = new TileRenderer();

    public FieldRender(Field field) {
        super();
        this.field = field;
        batch = new SpriteBatch();
        viewport = new ExtendViewport(25F, 25F, new OrthographicCamera());
        camera = (OrthographicCamera) viewport.getCamera();
        renderer = new ShapeRenderer();

        stage = new Stage(new ScreenViewport());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        table = new Table();
        table.setFillParent(true);
        table.top().left();
        table.add(textArea).grow().top().left().padTop(25).padLeft(20);

        stage.addActor(table);


        selectionGUI = new TroopSelectedGui();
        selectionGUI.setFillParent(true);
    }


    public void update(int screenWidth, int screenHeight) {
        viewport.update(screenWidth, screenHeight);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    public void debugRender() {

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.GREEN);
        /*
        for (Chunk chunk : field.getChunks()) {
            renderer.rect(chunk.getBounds().x, chunk.getBounds().y, chunk.getBounds().width, chunk.getBounds().height);
        }

         */

        for (GamePiece piece : field.getItems()) {
            renderer.setColor(Color.BLUE);
            if (piece.userData instanceof GamePieceActor) {
                ((GamePieceActor) piece.userData).debugRender(renderer);
            } else {
                renderer.circle(piece.getX(), piece.getY(), 0.25F, 20);
            }
        }
        renderer.end();
    }

    public void render(float delta) {
        Vector2 cameraMove = new Vector2();
        if (Gdx.input.isKeyPressed(Settings.moveUp.value)) {
            cameraMove.add(0, 1);
        }
        if (Gdx.input.isKeyPressed(Settings.moveDown.value)) {
            cameraMove.add(0, -1);
        }
        if (Gdx.input.isKeyPressed(Settings.moveLeft.value)) {
            cameraMove.add(-1, 0);
        }
        if (Gdx.input.isKeyPressed(Settings.moveRight.value)) {
            cameraMove.add(1, 0);
        }
        cameraMove.nor().scl(5 * camera.zoom * delta);
        ((ClientField) field).getPosition().add(cameraMove);

        // interpolate camera position
        ClientField clientField = (ClientField) field;
        camera.position.x = Interpolation.linear.apply(camera.position.x, clientField.getPosition().x, 0.4f);
        camera.position.y = Interpolation.linear.apply(camera.position.y, clientField.getPosition().y, 0.4f);
        camera.update();
        viewport.apply();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawTiles(delta);
        drawItems(delta);
        batch.end();

        debugRender();

        drawSelection(delta);

        drawProperties(delta);
        stage.act(delta);
        stage.getViewport().apply();

        stage.draw();


    }

    private void drawTiles(float delta) {
        Tile[][] tiles;
        int dx, dy;
        for (Chunk chunk : field.getChunks()) {
            dx = chunk.getChunkX() * chunk.getChunkSize();
            dy = chunk.getChunkY() * chunk.getChunkSize();
            tiles = chunk.getTiles();
            if (tiles == null) continue;
            for (int x = 0; x < tiles.length; x++) {
                for (int y = 0; y < tiles[x].length; y++) {
                    tileRenderer.render(batch, field, x + dx, y + dy);
                }
            }
        }
    }

    private void drawItems(float delta) {
        for (GamePiece item : field.getItems()) {
            if (item.userData instanceof GamePieceActor) {
                ((GamePieceActor) item.userData).render(batch, delta);
            }
        }
    }


    private void drawSelection(float delta) {
        if (rightClick) {
            updateEndDrag();
            float x = Math.min(startSelectionDrag.x, endSelectionDrag.x);
            float y = Math.min(startSelectionDrag.y, endSelectionDrag.y);
            float w = Math.abs(startSelectionDrag.x - endSelectionDrag.x);
            float h = Math.abs(startSelectionDrag.y - endSelectionDrag.y);

            // Gdx.gl.glEnable(GL30.GL_BLEND);
            // Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

            renderer.setProjectionMatrix(camera.combined);
            renderer.setAutoShapeType(true);

            renderer.begin(ShapeRenderer.ShapeType.Filled);

            renderer.setColor(0F, 0F, 1F, 0.3F);
            renderer.rect(x, y, w, h);

            renderer.set(ShapeRenderer.ShapeType.Line);
            renderer.setColor(0F, 0F, 1F, 1F);
            renderer.rect(x, y, w, h);

            renderer.end();
        }
    }

    private void drawProperties(float delta) {
        synchronized (selectedItems) {
            boolean redo = false;
            for (GamePiece item : selectedItems) {
                if (!writtenItems.contains(item, false) || item.getProperties().getModifiedProperties().size() > 0 || item.getProperties().getLocalModifiedProperties().size() > 0) {
                    redo = true;
                    item.getProperties().getModifiedProperties().clear();
                    item.getProperties().getLocalModifiedProperties().clear();
                }
            }
            if (!redo) {
                for (GamePiece item : writtenItems) {
                    if (!selectedItems.contains(item, false)) {
                        redo = true;
                        break;
                    }
                }
            }
            if (redo) {
                writtenItems.clear();
                writtenItems.addAll(selectedItems);
                StringBuilder builder = new StringBuilder();
                for (GamePiece item : selectedItems) {
                    builder.append(toStringGamePiece(item)).append("\n---\n");
                }
                textArea.setText(builder.toString());
                textArea.setFillParent(true);
                textArea.setMaxLength(10000);

                table.invalidateHierarchy();
            }
        }

    }


    private String toStringGamePiece(GamePiece item) {
        return item.getClass().getSimpleName() +
                "\n" +
                "GamePiece ID: " + item.getGameId() + '\n' +
                item.getProperties().toString();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        Settings.zoom.change(amountY * 0.01f + Settings.zoom.value);
        Settings.zoom.apply();
        return true;
    }

    Vector3 startMoveDrag = new Vector3();
    Vector3 endMoveDrag = new Vector3();
    Vector3 startSelectionDrag = new Vector3();
    Vector3 endSelectionDrag = new Vector3();
    boolean leftClick = false;
    boolean rightClick = false;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            leftClick = true;
            camera.unproject(startMoveDrag.set(screenX, screenY, 0));
            endMoveDrag.set(startMoveDrag);
            return true;
        } else if (button == Input.Buttons.RIGHT) {
            rightClick = true;
            camera.unproject(startSelectionDrag.set(screenX, screenY, 0));
            endSelectionDrag.set(startSelectionDrag);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (leftClick) {
            camera.unproject(endMoveDrag.set(screenX, screenY, 0));
            // translate the camera
            ((ClientField) field).getPosition().add(startMoveDrag.x - endMoveDrag.x, startMoveDrag.y - endMoveDrag.y);
            startMoveDrag.set(endMoveDrag);
            return true;
        }
        if (rightClick) {
            camera.unproject(endSelectionDrag.set(screenX, screenY, 0));
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT && rightClick) {
            Array<GamePiece> items = new Array<>();
            float x = Math.min(startSelectionDrag.x, endSelectionDrag.x);
            float y = Math.min(startSelectionDrag.y, endSelectionDrag.y);
            float w = Math.abs(startSelectionDrag.x - endSelectionDrag.x);
            float h = Math.abs(startSelectionDrag.y - endSelectionDrag.y);

            field.queryRect(x, y, w, h, items);

            synchronized (selectedItems) {
                for (GamePiece item : selectedItems) {
                    if (item instanceof Troop) {
                        item.getProperties().put("selected", false);
                    }
                }
                selectedItems.clear();
                selectedItems.addAll(items);
                selectionGUI.clearSelection();
                boolean contains = false;
                for (GamePiece item : selectedItems) {
                    if (item instanceof Troop) {
                        Troop troop = (Troop) item;
                        if (troop.getControllerId() == ((ClientField) field).getControllerId()) {
                            selectionGUI.addTroop(troop);
                            contains = true;
                        }
                    }
                }
                if (contains) {
                    selectionGUI.pack();
                    GameApp.addGameGui(selectionGUI);
                    // selectionGUI.invalidate();
                    // make the gui go to the right of the screen
                } else {
                    GameApp.removeGameGui(selectionGUI);
                }
            }
            rightClick = false;
            return true;
        } else if (button == Input.Buttons.LEFT && leftClick) {
            leftClick = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Group root = GameApp.guiStage().getRoot();
            if (settingsGUI == null) {
                settingsGUI = new SettingsGUI();
            }
            if (settingsGUI.getParent() == root) {
                root.removeActor(settingsGUI);
            } else {
                settingsGUI.pack();
                settingsGUI.setFillParent(true);
                // GameApp.addGameGui(settingsGUI);
                root.addActor(settingsGUI);
                GameApp.guiStage().setScrollFocus(settingsGUI);
                GameApp.guiStage().setKeyboardFocus(settingsGUI);
            }
        }
        return super.keyDown(keycode);
    }

    private void updateEndDrag() {
        camera.unproject(endMoveDrag.set(Gdx.input.getX(), Gdx.input.getY(), 0));
    }
}
