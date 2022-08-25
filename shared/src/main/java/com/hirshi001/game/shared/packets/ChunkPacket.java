package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.GamePieces;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.GamePiece;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.tiles.Tile;
import com.hirshi001.game.shared.tiles.Tiles;
import com.hirshi001.game.shared.util.HashedPoint;
import com.hirshi001.networking.packet.Packet;

import java.util.Set;

public class ChunkPacket extends Packet {

    public Chunk chunk;

    public ChunkPacket(){

    }

    public ChunkPacket(Chunk chunk){
        this.chunk = chunk;
    }

    @Override
    public void writeBytes(ByteBuffer out) {
        super.writeBytes(out);
        out.writeInt(chunk.getChunkX());
        out.writeInt(chunk.getChunkY());
        Tile[][] tiles = chunk.getTiles();
        Tile tile;
        int i, j;
        for (i = 0; i < chunk.getChunkSize(); i++) {
            for (j = 0; j < chunk.getChunkSize(); j++) {
                tile = tiles[i][j];
                if(tile==null) out.writeInt(-1);
                else out.writeInt(tile.getID());
            }
        }
        final Set<GamePiece> items = chunk.items;
        out.writeInt(items.size());
        for (GamePiece item : items) {
            out.writeInt(item.getID());
            item.writeBytes(out);
        }
        if(items.size()==7) {
            System.out.println("chunk packet: " + chunk.getChunkX() + " " + chunk.getChunkY());
        }
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        try {
            chunk = new Chunk(GameSettings.CHUNK_SIZE, new HashedPoint(in.readInt(), in.readInt()));
            int i, j;
            int id;
            for (i = 0; i < chunk.getChunkSize(); i++) {
                for (j = 0; j < chunk.getChunkSize(); j++) {
                    id = in.readInt();
                    if (id != -1) chunk.setTile(i, j, Tiles.TILE_REGISTRY.get(id));
                }
            }
            int size = in.readInt();
            if(size==7){
                System.out.println("Received chunk with 7 entities");
            }
            for (i = 0; i < size; i++) {
                id = in.readInt();
                GamePiece item = GamePieces.registry.get(id).get();
                item.readBytes(in);
                chunk.add(item);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
