package com.hirshi001.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.hirshi001.game.ClientField;
import com.hirshi001.game.render.tilerenderers.TileRenderers;
import com.hirshi001.game.shared.entities.Player;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.widgets.PropertiesTextArea;

import java.util.ArrayList;
import java.util.List;

public class FieldRender extends InputAdapter{

    SpriteBatch batch;
    public OrthographicCamera camera;
    ExtendViewport viewport;
    Field field;
    ShapeRenderer renderer;

    Stage stage;
    Table table;
    final List<GamePiece> selectedItems = new ArrayList<>();
    final List<GamePiece> writtenItems = new ArrayList<>();
    PropertiesTextArea textArea = new PropertiesTextArea("");

    public FieldRender(Field field){
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
    }


    public void update(int screenWidth, int screenHeight){
        viewport.update(screenWidth, screenHeight);
        stage.getViewport().update(screenWidth, screenHeight);
    }

    public void debugRender(){

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.GREEN);
        for(Chunk chunk : field.getChunks()){
            renderer.rect(chunk.getBounds().x, chunk.getBounds().y, chunk.getBounds().width, chunk.getBounds().height);
        }

        for(GamePiece piece: field.getItems()){
            renderer.setColor(Color.BLUE);
            if(piece.userData instanceof GamePieceActor){
                ((GamePieceActor)piece.userData).debugRender(renderer);
            }else{
                renderer.rect(piece.bounds.x, piece.bounds.y, piece.bounds.width, piece.bounds.height);
            }
        }
        renderer.end();
    }

    Vector3 target = new Vector3();
    public void render(float delta){


        ClientField cf = (ClientField) field;
        Player player = cf.getPlayer();
        if(player!=null){
            cf.getPosition().set(player.getCenterX(), player.getCenterY());

            target.set(player.getCenterX(), player.getCenterY(), 0);
            camera.position.interpolate(target, 0.1F, Interpolation.linear);
            camera.update();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawTiles(delta);
        drawItems(delta);
        batch.end();

        drawSelection(delta);

        drawProperties(delta);

    }

    public void getUserInput(float delta){
        Vector2 input = new Vector2();
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) input.x -= 1;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) input.x += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) input.y += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) input.y -= 1;
        if(input.equals(Vector2.Zero)) return;
        input.nor();
        camera.position.add(input.x * delta * 100, input.y * delta * 100, 0);
        ((ClientField)field).getPosition().set(camera.position.x, camera.position.y);
        camera.update();
    }

    private void drawTiles(float delta){
        Tile[][] tiles;
        int dx, dy;
        for(Chunk chunk: field.getChunks()){
            dx = chunk.getChunkX() * chunk.getChunkSize();
            dy = chunk.getChunkY() * chunk.getChunkSize();
            tiles = chunk.getTiles();
            if(tiles==null) continue;
            for(int x = 0; x < tiles.length; x++){
                for(int y = 0; y < tiles[x].length; y++){
                    if(tiles[x][y] != null){
                        TileRenderers.TILE_RENDERERS.get(tiles[x][y].getID()).render(batch, field, x + dx, y + dy);
                    }else{
                        TileRenderers.DEFAULT.render(batch, field, x + dx, y + dy);
                    }
                }
            }
        }
    }

    private void drawItems(float delta){
        for(GamePiece item : field.getItems()){
            if(item.userData instanceof GamePieceActor){
                ((GamePieceActor)item.userData).render(batch, delta);
            }
        }
    }


    private void drawSelection(float delta){
        if(dragging) {
            updateEndDrag();
            float x = Math.min(startDrag.x, endDrag.x);
            float y = Math.min(startDrag.y, endDrag.y);
            float w = Math.abs(startDrag.x - endDrag.x);
            float h = Math.abs(startDrag.y - endDrag.y);

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

    private void drawProperties(float delta){
        synchronized (selectedItems){
            boolean redo = false;
            for(GamePiece item : selectedItems){
                if(!writtenItems.contains(item) || item.getProperties().getModifiedProperties().size()>0){
                    redo = true;
                    item.getProperties().getModifiedProperties().clear();
                }
            }
            if(!redo){
                for(GamePiece item : writtenItems){
                    if(!selectedItems.contains(item)) {
                        redo = true;
                        break;
                    }
                }
            }
            if(redo){
                writtenItems.clear();
                writtenItems.addAll(selectedItems);
                StringBuilder builder = new StringBuilder();
                for(GamePiece item : selectedItems){
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


    private String toStringGamePiece(GamePiece item){
        return item.getClass().getSimpleName() +
                "\n" +
                "GamePiece ID: " + item.getGameId() + '\n' +
                item.getProperties().toString();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom += amountY/100F;
        camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 5f);
        camera.update();
        return true;
    }

    boolean dragging = false;
    Vector3 startDrag = new Vector3();
    Vector3 endDrag = new Vector3();
    int dragPointer = -1;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.RIGHT){
            dragPointer = pointer;
            System.out.println("Drag start " + dragPointer);
            camera.unproject(startDrag.set(screenX, screenY, 0));
            endDrag.set(startDrag);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(dragPointer!=-1) {
            camera.unproject(endDrag.set(screenX, screenY, 0));
            dragging = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(button==Input.Buttons.RIGHT){
            dragPointer = -1;
            ArrayList<GamePiece> items = new ArrayList<>();
            if (dragging) {
                float x = Math.min(startDrag.x, endDrag.x);
                float y = Math.min(startDrag.y, endDrag.y);
                float w = Math.abs(startDrag.x - endDrag.x);
                float h = Math.abs(startDrag.y - endDrag.y);

                field.queryRect(x, y, w, h, null, items);
                dragging = false;
            } else {
                Vector3 pos = new Vector3(screenX, screenY, 0);
                camera.unproject(pos);
                field.queryPoint(pos.x, pos.y, null, items);

            }

            synchronized (selectedItems) {
                selectedItems.clear();
                selectedItems.addAll(items);
            }

        }

        return true;
    }

    private void updateEndDrag(){
        if(dragging){
            camera.unproject(endDrag.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        }
    }
}
