package com.hirshi001.game.shared.settings;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.hirshi001.buffer.bufferfactory.BufferFactory;
import com.hirshi001.game.shared.control.AttackTroopMovement;
import com.hirshi001.game.shared.control.FollowLeaderMovement;
import com.hirshi001.game.shared.control.MoveTroopMovement;
import com.hirshi001.game.shared.util.RunnablePoster;
import com.hirshi001.game.shared.util.props.PropertiesManager;
import com.hirshi001.game.shared.util.serializer.*;

public class GameSettings {

    public static final float CELL_SIZE =8F;
    public static final int CHUNK_SIZE = 16;
    public static RunnablePoster runnablePoster;
    public static final int TICKS_PER_SECOND = 20;
    public static final float SECONDS_PER_TICK = 1F / TICKS_PER_SECOND;
    public static BufferFactory BUFFER_FACTORY;

    public static final PropertiesManager MANAGER = new PropertiesManager();

    public static void registerSerializers() {
        MANAGER.register(Boolean.class, new BooleanSerializer(), 0);
        MANAGER.register(Byte.class, new ByteSerializer(), 1);
        MANAGER.register(Short.class, new ShortSerializer(), 2);
        MANAGER.register(Integer.class, new IntegerSerializer(), 3);
        MANAGER.register(Long.class, new LongSerializer(), 4);
        MANAGER.register(Double.class, new DoubleSerializer(), 5);
        MANAGER.register(Float.class, new FloatSerializer(), 6);
        MANAGER.register(LinePath.class, new PathSerializer(), 7);
        MANAGER.register(String.class, new StringSerializer(), 8);
        MANAGER.register(Vector2.class, new VectorSerializer(), 9);

        MANAGER.register(MoveTroopMovement.class, new MoveTroopMovementSerializer(), 32);
        MANAGER.register(AttackTroopMovement.class, new AttackTroopMovementSerializer(), 33);
        MANAGER.register(FollowLeaderMovement.class, new FollowLeaderMovementSerializer(), 34);
    }


}
