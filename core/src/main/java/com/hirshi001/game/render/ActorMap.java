package com.hirshi001.game.render;

import com.hirshi001.game.shared.entities.Player;
import com.hirshi001.game.shared.game.GamePiece;

import java.util.HashMap;
import java.util.Map;

public class ActorMap {

    public static final Map<Class, GamePieceActorSupplier> MAP = new HashMap<>();

    public static void register(){
        register(Player.class, PlayerActor::new);
    }

    public static <T extends GamePiece> void register(Class<T> clazz, GamePieceActorSupplier<T> supplier){
        MAP.put(clazz, supplier);
    }

    public static <T extends GamePiece> GamePieceActor<T> get(T gamePiece){
        if(gamePiece == null)
            return null;
        if(!MAP.containsKey(gamePiece.getClass()))
            return null;
        return MAP.get(gamePiece.getClass()).get(gamePiece);
    }


}

interface GamePieceActorSupplier<T extends GamePiece> {
    GamePieceActor<T> get(T gamePiece);
}