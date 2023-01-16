package com.hirshi001.game;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class GameResources {


    private final String dir, resourcesDir;
    private final AssetManager assetManager;

    private final Map<String, String> assets;
    private final Map<String, String> aliases;
    private final String atlasName;

    public GameResources(JsonValue resourceMap) {
        this.assetManager = new AssetManager();
        this.assets = new HashMap<>();

        // ROOT DIR
        dir = resourceMap.getString("dir");

        // ATLAS
        {
            String aName = resourceMap.getString("atlas", null);
            if (aName != null) {
                this.atlasName = dir + "/" + aName;
                System.out.println("Loading atlas: " + this.atlasName);
                assetManager.load(this.atlasName, TextureAtlas.class);
            }else this.atlasName = null;
        }

        // RESOURCES DIRECTORY
        resourcesDir = resourceMap.getString("resourcesDir");


        // ALIASES
        aliases = new HashMap<>();
        for (JsonValue alias : resourceMap.get("aliases")) {
            aliases.put(alias.name, alias.asString());
        }


        // RESOURCES
        String rootPath = dir + "/" + resourcesDir;

        for (JsonValue resource : resourceMap.get("resources")) {
            String type = resource.asString();
            type = aliases.getOrDefault(type, type);
            String path = rootPath + "/" + resource.name;

            Class<?> clazz;
            try {
                clazz = getClass(type);

                // cases for BitMapFont in TextureAtlas
                if (clazz == BitmapFont.class) {
                    BitmapFontLoader.BitmapFontParameter parameter = new BitmapFontLoader.BitmapFontParameter();
                    if(atlasName!=null) parameter.atlasName = atlasName;
                    assetManager.load(path, BitmapFont.class, parameter);
                } else {
                    assetManager.load(path, clazz);
                }
            } catch (Exception e) {
                Gdx.app.error("GameResources", "Failed to load resource: " + path + " of type: " + type);
                // e.printStackTrace();
                // return;
            }
        }


        for (JsonValue map : resourceMap.get("map")) {
            String name = map.name;
            String path = dir + "/" + resourcesDir + "/" + map.asString();
            assets.put(name, path);
        }
    }

    public TextureRegion getFromAtlas(String name) {
        if(atlasName==null) throw new IllegalStateException("No atlas was loaded");

        return assetManager.get(atlasName, TextureAtlas.class).findRegion(name);
    }

    public boolean update() throws GdxRuntimeException {
        if (assets == null) {
            return false;
        }
        return assetManager.update();
    }

    public boolean isFinished() {
        return assetManager.isFinished();
    }

    public <T> T get(String name) {
        try {
            return assetManager.get(assets.get(name));
        }catch (Exception e){
            throw e;
        }
    }

    public void finishLoadingAsset(String name) throws IOException {
        assetManager.finishLoadingAsset(assets.get(name));
    }

    public void finishLoading() throws IOException {
        assetManager.finishLoading();
    }

    public void set(String name, String value) {
        assets.put(name, value);
    }

    public void addAsset(String path, Class<?> clazz) {
        assetManager.load(path, clazz);
    }

    public void addAsset(String name, String path, Class<?> clazz) {
        assetManager.load(path, clazz);
        assets.put(name, path);
    }

    public void addName(String name, String path) {
        assets.put(name, path);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    private Class<?> getClass(String name) throws ReflectionException {
        if (name.equals("com.badlogic.gdx.graphics.Texture")) {
            return Texture.class;
        }
        if (name.equals("com.badlogic.gdx.audio.Music")) {
            return Music.class;
        }
        if (name.equals("com.badlogic.gdx.audio.Sound")) {
            return Sound.class;
        }
        if (name.equals("com.badlogic.gdx.graphics.g2d.BitmapFont")) {
            return BitmapFont.class;
        }
        return ClassReflection.forName(name);
    }

}
