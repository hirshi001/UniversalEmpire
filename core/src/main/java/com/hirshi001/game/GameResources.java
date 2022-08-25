package com.hirshi001.game;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GameResources {


    private final String dir;
    private final AssetManager assetManager;

    private final Map<String, String> assets;
    private final Map<String, String> aliases;
    public GameResources(JsonValue resourceMap) {
        dir = resourceMap.getString("dir");
        aliases = new HashMap<>();


        for(JsonValue alias : resourceMap.get("aliases")) {
            aliases.put(alias.name, alias.asString());
        }

        this.assetManager = new AssetManager();
        this.assets = new HashMap<>();

        for(JsonValue resource : resourceMap.get("resources")) {
            String type = resource.asString();
            type = aliases.getOrDefault(type, type);
            String path = dir + resource.name;

            Class<?> clazz;
            try {
                clazz = getClass(type);
                assetManager.load(path, clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }


        for(JsonValue map : resourceMap.get("map")) {
            String name = map.name;
            String path = dir + map.asString();
            assets.put(name, path);
        }
    }



    public boolean update() throws IOException {
        if(assets == null){
            return false;
        }
        return assetManager.update();
    }

    public boolean isFinished(){
        return assetManager.isFinished();
    }

    public <T> T get(String name){
        return assetManager.get(assets.get(name));
    }

    public void finishLoadingAsset(String name) throws IOException {
        assetManager.finishLoadingAsset(assets.get(name));
    }

    public void finishLoading() throws IOException {
        assetManager.finishLoading();
    }

    public void set(String name, String value){
        assets.put(name, value);
    }

    public void addAsset(String path, Class<?> clazz){
        assetManager.load(path, clazz);
    }

    public void addAsset(String name, String path, Class<?> clazz){
        assetManager.load(path, clazz);
        assets.put(name, path);
    }

    public void addName(String name, String path){
        assets.put(name, path);
    }

    public AssetManager getAssetManager(){
        return assetManager;
    }

    private Class<?> getClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

}