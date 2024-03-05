package com.hirshi001.game.gwt;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class CustomOutputStream extends OutputStream {

    Consumer<String> out;
    StringBuilder buffer = new StringBuilder();

    public CustomOutputStream(Consumer<String> out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        if(b == '\n') {
            flush();
        } else {
            buffer.append((char) b);
        }
    }

    @Override
    public void write(byte @NotNull [] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte @NotNull [] b, int off, int len) throws IOException {
        for (int i = off; i < len; i++) {
            if(b[i] == '\n') {
                flush();
            } else {
                buffer.append((char) b[i]);
            }
        }
    }

    @Override
    public void flush() throws IOException {
        out.accept(buffer.toString());
        buffer.setLength(0);
    }
}
