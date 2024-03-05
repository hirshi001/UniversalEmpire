package com.hirshi001.game.server;

import com.hirshi001.game.shared.entities.GamePiece;
import com.hirshi001.game.shared.entities.TestGamePiece;
import com.hirshi001.game.shared.game.Chunk;
import com.hirshi001.game.shared.game.Field;
import com.hirshi001.game.shared.settings.GameSettings;
import com.hirshi001.game.shared.tiles.Tile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class CommandHandler implements Runnable {

    public final Set<Class> watchedPackets = Collections.synchronizedSet(new HashSet<>());
    private final Field field;

    public CommandHandler(Field field) {
        this.field = field;
    }

    private void performCommand(String[] args) {
        if (args.length == 0) return;
        String command = args[0];
        if (command.equalsIgnoreCase("exit")) {
            System.exit(0);
        }
        if (command.equalsIgnoreCase("chunk")) {
            chunkCommand(args);
        }
        if (command.equalsIgnoreCase("spawn")) {
            spawnCommand(args);
        }
        if (command.equalsIgnoreCase("setProp")) {
            setPropertyCommand(args);
        }
        if (command.equalsIgnoreCase("remove")) {
            removeCommand(args);
        }
        if (command.equalsIgnoreCase("watchPacket")) {
            watchPacketCommand(args);
        }
        if (command.equalsIgnoreCase("unwatchPacket")) {
            unwatchPacketCommand(args);
        }
    }

    private void chunkCommand(String[] args) {
        if (args.length != 3) return;
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);

        Chunk chunk = field.addChunk(x, y);
        if (chunk == null) System.out.println("Chunk is null");
        Tile[][] tiles = chunk.getTiles();
        System.out.println("Chunk " + x + "," + y + ":");
        System.out.println("Tiles: ");
        for (int i = 0; i < GameSettings.CHUNK_SIZE; i++) {
            for (int j = 0; j < GameSettings.CHUNK_SIZE; j++) {
                System.out.print(tiles[j][i].getID() + " ");
            }
            System.out.println();
        }
        System.out.println("GamePieces: ");
        for (GamePiece piece : chunk.items) {
            System.out.println(piece.getClass().getSimpleName() + ": " + piece.getGameId());
        }
    }

    private void spawnCommand(String[] args) {
        if (args.length != 4) return;
        String name = args[1];
        float x = Float.parseFloat(args[2]);
        float y = Float.parseFloat(args[3]);

        GamePiece piece;
        if (name.equals("TestGamePiece")) {
            piece = new TestGamePiece();
            piece.getPosition().set(x, y);
        } else {
            System.out.println("Unknown game piece: " + name);
            return;
        }

        field.addGamePiece(piece);
        System.out.println("Spawned " + piece.getClass().getSimpleName() + ": " + piece.getGameId());
    }

    private void setPropertyCommand(String[] args) {
        if (args.length < 4) return;
        int gamePieceId = Integer.parseInt(args[1]);
        String property = args[2];

        StringBuilder valueBuilder = new StringBuilder();
        for (int i = 3; i < args.length - 1; i++) {
            valueBuilder.append(args[i]).append(" ");
        }
        valueBuilder.append(args[args.length - 1]);
        String value = valueBuilder.toString();

        Object v = null;
        if (value.equals("true")) v = true;
        else if (value.equals("false")) v = false;
        else {
            try {
                v = Integer.parseInt(value);
            } catch (Exception ignored) {
            }
            if (v == null) {
                try {
                    v = Float.parseFloat(value);
                } catch (Exception ignored) {
                }
            }
            if (v == null) {
                v = value;
            }
        }

        GamePiece piece = field.getGamePiece(gamePieceId);
        if (piece == null) {
            System.out.println("Game piece not found: " + gamePieceId);
            return;
        }
        final Object vf = v;
        GameSettings.runnablePoster.postRunnable(() -> {
            piece.getProperties().put(property, vf);
        });
    }

    private void removeCommand(String[] args) {
        if (args.length != 2) return;
        int gamePieceId = Integer.parseInt(args[1]);
        GamePiece piece = field.getGamePiece(gamePieceId);
        if (piece == null) {
            System.out.println("Game piece not found: " + gamePieceId);
            return;
        }
        field.removeGamePiece(piece);
    }

    private void watchPacketCommand(String[] args) {
        if (args.length != 2) return;
        String packetName = args[1];

        String name = "com.hirshi001.game.shared.packets." + packetName;
        try {
            Class<?> clazz = Class.forName(name);
            watchedPackets.add(clazz);
            System.out.println("Watching packet: " + name);
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class: " + name);
        }
    }

    private void unwatchPacketCommand(String[] args) {
        if (args.length != 2) return;
        String packetName = args[1];

        String name = "com.hirshi001.game.shared.packets." + packetName;
        try {
            Class<?> clazz = Class.forName(name);
            watchedPackets.remove(clazz);
            System.out.println("Watching packet: " + name);
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class: " + name);
        }
    }


    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] args = line.split(" ");
            try {
                performCommand(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
