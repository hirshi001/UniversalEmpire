package com.hirshi001.game.server;

import com.hirshi001.game.shared.entities.Player;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.util.Point;
import com.hirshi001.networking.network.channel.Channel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlayerData {

    public Player player;
    public Field field;
    public Channel channel;
    public boolean hasShot;

    public Set<Point> trackedChunks = Collections.synchronizedSet(new HashSet<>());
    public Set<Point> softTrackedChunks = Collections.synchronizedSet(new HashSet<>());


}
