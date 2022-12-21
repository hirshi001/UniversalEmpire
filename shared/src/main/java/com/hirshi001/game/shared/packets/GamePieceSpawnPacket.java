package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.GamePieces;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.networking.packet.Packet;

public class GamePieceSpawnPacket extends Packet {

    public GamePiece gamePiece;

    public GamePieceSpawnPacket() {
        super();
    }

    public GamePieceSpawnPacket(GamePiece gamePiece) {
        super();
        this.gamePiece = gamePiece;

    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeInt(gamePiece.getID());
        gamePiece.writeBytes(out);
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        gamePiece = GamePieces.registry.get(in.readInt()).get();
        gamePiece.readBytes(in);
    }
}
