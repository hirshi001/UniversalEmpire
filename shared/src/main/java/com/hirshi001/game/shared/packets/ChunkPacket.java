package com.hirshi001.game.shared.packets;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.game.shared.entities.GamePieces;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.entities.GamePiece;
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

        // write chunk
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

        // write game pieces
        final Set<GamePiece> items = chunk.items;
        out.writeInt(items.size());
        for (GamePiece item : items) {
            out.writeInt(item.getID());
            item.writeBytes(out);
        }

        // write tile game pieces

        int[][] tileEntities = chunk.getTileEntities();
        for (i = 0; i < chunk.getChunkSize(); i++) {
            for (j = 0; j < chunk.getChunkSize(); j++) {
                out.writeInt(tileEntities[i][j]);
            }
        }
    }

    @Override
    public void readBytes(ByteBuffer in) {
        super.readBytes(in);
        int size=0;
        HashedPoint chunkPos = new HashedPoint(in.readInt(), in.readInt());
        try {
            chunk = new Chunk(GameSettings.CHUNK_SIZE, chunkPos);

            // read chunks
            int i, j;
            int id = 0;
            for (i = 0; i < chunk.getChunkSize(); i++) {
                for (j = 0; j < chunk.getChunkSize(); j++) {
                    id = in.readInt();
                    if (id != -1) chunk.setTile(i, j, Tiles.getInstance().tileRegistry.get(id));
                }
            }

            // read game pieces
            GamePiece item;
            size = in.readInt();
            for (i = 0; i < size; i++) {
                id = in.readInt();
                item = GamePieces.registry.get(id).get();
                item.readBytes(in);
                chunk.add(item);
            }

            // read tile game pieces
            int[][] tileEntities = chunk.getTileEntities();
            for (i = 0; i < chunk.getChunkSize(); i++) {
                for (j = 0; j < chunk.getChunkSize(); j++) {
                    tileEntities[i][j] = in.readInt();
                }
            }

        }catch (Exception e){
            System.out.println("size: " + size);
            System.out.println("chunk: " + chunkPos);
            e.printStackTrace();
        }
    }

}
