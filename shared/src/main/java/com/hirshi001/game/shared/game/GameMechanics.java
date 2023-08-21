package com.hirshi001.game.shared.game;

import com.badlogic.gdx.utils.Array;
import com.hirshi001.game.shared.control.TroopGroup;

public abstract class GameMechanics {

    public Field field;

    public void setField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public abstract void createTroopGroup(int playerId, String name, Array<Integer> troopIds, int leaderId);

    public abstract void deleteTroopGroup(int playerId, String name);

    public abstract TroopGroup getTroopGroup(int playerId, String name);

    public abstract void addTroopsToGroup(int playerId, String name, Array<Integer> troopIds);

    public abstract void removeTroopsFromGroup(int playerId, String name, Array<Integer> troopIds);

    public abstract void moveTroopGroup(int playerId, String name, float x, float y);
}
