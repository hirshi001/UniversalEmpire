package com.hirshi001.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.hirshi001.game.ClientField;
import com.hirshi001.game.GameApp;
import com.hirshi001.game.render.tilerenderers.TileRenderers;
import com.hirshi001.game.screens.maingamescreen.GameGUI;
import com.hirshi001.game.screens.maingamescreen.TroopSelectedGui;
import com.hirshi001.game.shared.entities.troop.Troop;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.widgets.PropertiesTextArea;
import com.hirshi001.game.widgets.Styles;

import java.util.ArrayList;
import java.util.List;

public class FieldRender extends InputAdapter {

    SpriteBatch batch;
    public OrthographicCamera camera;
    ExtendViewport viewport;
    Field field;
    ShapeRenderer renderer;

    Stage stage;
    Table table;
    public final List<GamePiece> selectedItems = new ArrayList<>();
    public final List<GamePiece> writtenItems = new ArrayList<>();
    PropertiesTextArea textArea = new PropertiesTextArea("");

    TroopSelectedGui selectionGUI;

    public FieldRender(Field field) {
        super();
        this.field = field;
        batch = new SpriteBatch();
        viewport = new ExtendViewport(25F, 25F, new OrthographicCamera());
        camera = (OrthographicCamera) viewport.getCamera();
        renderer = new ShapeRenderer();

        stage = new Stage();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(textArea).grow().top().left();

        selectionGUI = new TroopSelectedGui();
        selectionGUI.setFillParent(true);
    }


    public void update(int screenWidth, int screenHeight) {
        viewport.update(screenWidth, screenHeight);
        stage.getViewport().update(screenWidth, screenHeight);
    }

    public void debugRender() {

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.GREEN);
        for (Chunk chunk : field.getChunks()) {
            renderer.rect(chunk.getBounds().x, chunk.getBounds().y, chunk.getBounds().width, chunk.getBounds().height);
        }

        for (GamePiece piece : field.getItems()) {
            renderer.setColor(Color.BLUE);
            if (piece.userData instanceof GamePieceActor) {
                ((GamePieceActor) piece.userData).debugRender(renderer);
            } else {
                renderer.rect(piece.bounds.x, piece.bounds.y, piece.bounds.width, piece.bounds.height);
            }
        }
        renderer.end();
    }

    public void render(float delta) {

        // interpolate camera position
        ClientField clientField = (ClientField) field;
        camera.position.x = Interpolation.linear.apply(camera.position.x, clientField.getPosition().x, 0.4f);
        camera.position.y = Interpolation.linear.apply(camera.position.y, clientField.getPosition().y, 0.4f);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawTiles(delta);
        drawItems(delta);
        batch.end();
        debugRender();

        drawSelection(delta);

        drawProperties(delta);


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

                    // TileRenderers.DEFAULT.render(batch, field, x + dx, y + dy);
                    // if (true) continue;
                    if (tiles[x][y] != null) {
                        TileRenderers.TILE_RENDERERS.get(tiles[x][y].getID()).render(batch, field, x + dx, y + dy);
                    } else {
                        TileRenderers.DEFAULT.render(batch, field, x + dx, y + dy);
                    }
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
                if (!writtenItems.contains(item) || item.getProperties().getModifiedProperties().size() > 0 || item.getProperties().getLocalModifiedProperties().size() > 0) {
                    redo = true;
                    item.getProperties().getModifiedProperties().clear();
                    item.getProperties().getLocalModifiedProperties().clear();
                }
            }
            if (!redo) {
                for (GamePiece item : writtenItems) {
                    if (!selectedItems.contains(item)) {
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
            }
        }

        stage.act(delta);
        stage.draw();
    }


    private String toStringGamePiece(GamePiece item) {
        return item.getClass().getSimpleName() +
                "\n" +
                "GamePiece ID: " + item.getGameId() + '\n' +
                item.getProperties().toString();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom += amountY / 100F;
        camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 5f);
        camera.update();
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
            ArrayList<GamePiece> items = new ArrayList<>();
            float x = Math.min(startSelectionDrag.x, endSelectionDrag.x);
            float y = Math.min(startSelectionDrag.y, endSelectionDrag.y);
            float w = Math.abs(startSelectionDrag.x - endSelectionDrag.x);
            float h = Math.abs(startSelectionDrag.y - endSelectionDrag.y);

            field.queryRect(x, y, w, h, null, items);

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
                }else {
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

    private void updateEndDrag() {
        camera.unproject(endMoveDrag.set(Gdx.input.getX(), Gdx.input.getY(), 0));
    }
}
