package com.hirshi001.game.shared.entities;

import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.registry.DefaultRegistry;
import com.hirshi001.game.shared.registry.ID;
import com.hirshi001.game.shared.registry.ObjectHolderSupplier;
import com.hirshi001.game.shared.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GamePieces {

    public static final Registry<ObjectHolderSupplier<GamePiece>> registry = new DefaultRegistry<>();
    public static final Map<Class, Integer> ID_MAP = new HashMap<>();

    public static ObjectHolderSupplier<CircleGamePiece> CIRCLE_GAME_PIECE;
    public static ObjectHolderSupplier<Player> PLAYER;
    public static ObjectHolderSupplier<TestGamePiece> TEST_GAME_PIECE;
    public static ObjectHolderSupplier<Fireball> FIREBALL;

    public static void register(){
        CIRCLE_GAME_PIECE = register(CircleGamePiece.class, CircleGamePiece::new, 0);
        PLAYER = register(Player.class, Player::new, 1);
        TEST_GAME_PIECE = register(TestGamePiece.class, TestGamePiece::new, 2);
        FIREBALL = register(Fireball.class, Fireball::new, 3);
    }

    private static <T extends GamePiece> ObjectHolderSupplier register(Class<T> clazz, Supplier<T> entitySupplier, int id){
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

    public static int getId(GamePiece gamePiece){
        return getId(gamePiece.getClass());
    }

    public static int getId(Class<? extends GamePiece> clazz){
        return ID_MAP.get(clazz);
    }

}
