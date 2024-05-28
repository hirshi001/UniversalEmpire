package com.hirshi001.game.shared.settings;

import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.hirshi001.betternetworkingutil.ByteBufSerializer;
import com.hirshi001.buffer.bufferfactory.BufferFactory;
import com.hirshi001.game.shared.control.AttackTroopMovement;
import com.hirshi001.game.shared.control.FollowLeaderMovement;
import com.hirshi001.game.shared.control.MoveTroopMovement;
import com.hirshi001.game.shared.util.Range;
import com.hirshi001.game.shared.util.RunnablePoster;
import com.hirshi001.game.shared.util.props.PropertiesManager;
import com.hirshi001.game.shared.util.serializer.*;

public class GameSettings {

    public static final float CELL_SIZE = 8F;
    public static final int CHUNK_SIZE = 16;
    public static RunnablePoster runnablePoster;
    public static final int TICKS_PER_SECOND = 20;
    public static final float SECONDS_PER_TICK = 1F / TICKS_PER_SECOND;
    public static BufferFactory BUFFER_FACTORY;
    public static final int TILE_TEXTURE_SIZE = 16;

    public static final PropertiesManager MANAGER = new PropertiesManager();

    public static void registerSerializers() {
        int i = 0;
        register(Boolean.class, Serializers.BOOLEAN, i++);
        register(Byte.class, Serializers.BYTE, i++);
        register(Short.class, Serializers.SHORT, i++);
        register(Integer.class, Serializers.INTEGER, i++);
        register(Long.class, Serializers.LONG, i++);
        register(Double.class, Serializers.DOUBLE, i++);
        register(Float.class, Serializers.FLOAT, i++);
        register(Character.class, Serializers.CHARACTER, i++);
        register(String.class, Serializers.STRING, i++);
        register(Vector2.class, Serializers.VECTOR2, i++);
        register(Range.class, Serializers.RANGE, i++);
        register(LinePath.class, Serializers.PATH, i++);

        i = 32;
        register(MoveTroopMovement.class, new MoveTroopMovementSerializer(), i++);
        register(AttackTroopMovement.class, new AttackTroopMovementSerializer(), i++);
        register(FollowLeaderMovement.class, new FollowLeaderMovementSerializer(), i++);
    }

    public static <T> void register(Class<T> clazz, ByteBufSerializer<T> serializer, int id) {
        MANAGER.register(clazz, serializer, id);
    }

}
