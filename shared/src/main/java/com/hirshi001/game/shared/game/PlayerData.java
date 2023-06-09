package com.hirshi001.game.shared.game;

import com.hirshi001.game.shared.control.TroopGroup;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.networking.network.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    public Field field;
    public Channel channel;
    public int controllerId;

    public Map<String, TroopGroup> troopGroups = new ConcurrentHashMap<>();
    public Set<Point> trackedChunks = new HashSet<>();
    public Set<Point> softTrackedChunks = new HashSet<>();

    @Override
    public int hashCode() {
        return controllerId;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerData && ((PlayerData) obj).controllerId == controllerId;
    }
}
