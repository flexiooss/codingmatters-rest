package org.codingmatters.rest.undertow;

import org.codingmatters.rest.api.SseChannel;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UndertowSseChannel implements SseChannel {

    private final OutputStream out;
    private volatile boolean open = true;
    private Runnable closeHandler;

    public UndertowSseChannel(OutputStream out) {
        this.out = out;
    }

    @Override
    public SseChannel send(String event, String data) throws IOException {
        write("event: " + event + "\n" + "data: " + data + "\n\n");
        return this;
    }

    @Override
    public SseChannel send(String data) throws IOException {
        write("data: " + data + "\n\n");
        return this;
    }

    @Override
    public SseChannel comment(String comment) throws IOException {
        write(": " + comment + "\n\n");
        return this;
    }

    @Override
    public SseChannel id(String id) throws IOException {
        write("id: " + id + "\n\n");
        return this;
    }

    @Override
    public SseChannel retry(long milliseconds) throws IOException {
        write("retry: " + milliseconds + "\n\n");
        return this;
    }

    @Override
    public synchronized void close() {
        if (!this.open) return;
        this.open = false;
        try {
            out.close();
        } catch (IOException ignored) {
        }
        if (this.closeHandler != null) {
            this.closeHandler.run();
        }
    }

    @Override
    public SseChannel onClose(Runnable handler) {
        this.closeHandler = handler;
        return this;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    private synchronized void write(String text) throws IOException {
        if (!this.open) {
            throw new IOException("SSE channel is closed");
        }
        out.write(text.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}
