package com.hirshi001.game.render;

import com.hirshi001.game.shared.entities.*;
import com.hirshi001.game.shared.entities.troop.Knight;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ActorMap {

    private static final Map<Class, GamePieceActorSupplier> MAP = new HashMap<>();

    public static void register(){
        register(Fireball.class, FireballActor::new);
        register(CircleGamePiece.class, CircleGamePieceActor::new);
        register(Knight.class, KnightActor::new);
        register(SolidTile.class, null);
        register(Stone.class, StoneActor::new);

        KnightActor.loadAnimation();
    }

    private static <T extends GamePiece> void register(Class<T> clazz, GamePieceActorSupplier<T> supplier){
        MAP.put(clazz, supplier);
    }

    public static <T extends GamePiece> GamePieceActor<T> get(T gamePiece, FieldRender render){
        if(gamePiece == null)
            return null;
        if(MAP.get(gamePiece.getClass()) == null)
            return null;
        return MAP.get(gamePiece.getClass()).get(gamePiece, render);
    }


}

interface GamePieceActorSupplier<T extends GamePiece> {
    GamePieceActor<T> get(T gamePiece, FieldRender render);
}
