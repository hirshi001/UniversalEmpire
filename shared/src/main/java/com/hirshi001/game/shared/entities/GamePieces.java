package com.hirshi001.game.shared.entities;

import com.hirshi001.game.shared.entities.troop.Knight;
import com.hirshi001.game.shared.registry.DefaultRegistry;
import com.hirshi001.game.shared.registry.ID;
import com.hirshi001.game.shared.registry.ObjectHolderSupplier;
import com.hirshi001.game.shared.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GamePieces {

    public static final Registry<ObjectHolderSupplier<GamePiece>> registry = new DefaultRegistry<>();

    @SuppressWarnings("rawtypes")
    public static final Map<Class, Integer> ID_MAP = new HashMap<>();

    public static ObjectHolderSupplier<CircleGamePiece> CIRCLE_GAME_PIECE;
    public static ObjectHolderSupplier<TestGamePiece> TEST_GAME_PIECE;
    public static ObjectHolderSupplier<Fireball> FIREBALL;
    public static ObjectHolderSupplier<Knight> KNIGHT;
    public static ObjectHolderSupplier<SolidTile> SOLID_TILE;
    public static ObjectHolderSupplier<Stone> STONE;

    @SuppressWarnings({"unchecked"})
    public static void register() {
        int id = 0;
        CIRCLE_GAME_PIECE = register(CircleGamePiece.class, CircleGamePiece::new, id++);
        TEST_GAME_PIECE = register(TestGamePiece.class, TestGamePiece::new, id++);
        FIREBALL = register(Fireball.class, Fireball::new, id++);
        KNIGHT = register(Knight.class, Knight::new, id++);
        SOLID_TILE = register(SolidTile.class, SolidTile::new, id++);
        STONE = register(Stone.class, Stone::new, id++);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends GamePiece> ObjectHolderSupplier register(Class<T> clazz, Supplier<T> entitySupplier, int id) {
        ObjectHolderSupplier supplier = new ObjectHolderSupplier() {
            @Override
            protected ID create() {
                return entitySupplier.get();
            }
        };
        registry.register(supplier, id);
        ID_MAP.put(clazz, id);
        return supplier;
    }

    public static int getId(GamePiece gamePiece) {
        return getId(gamePiece.getClass());
    }

    public static int getId(Class<? extends GamePiece> clazz) {
        return ID_MAP.get(clazz);
    }

}
