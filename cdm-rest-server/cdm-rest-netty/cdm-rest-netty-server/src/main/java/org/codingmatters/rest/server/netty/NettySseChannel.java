package org.codingmatters.rest.server.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import org.codingmatters.rest.api.SseChannel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NettySseChannel implements SseChannel {

    private final ChannelHandlerContext ctx;
    private volatile boolean open = true;
    private volatile Runnable onCloseHandler;

    public NettySseChannel(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public SseChannel send(String event, String data) throws IOException {
        write("event: " + event + "\ndata: " + data + "\n\n");
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
        write("id: " + id + "\n");
        return this;
    }

    @Override
    public SseChannel retry(long milliseconds) throws IOException {
        write("retry: " + milliseconds + "\n\n");
        return this;
    }

    private synchronized void write(String text) throws IOException {
        if (!this.open) {
            throw new IOException("SSE channel is closed");
        }
        ctx.writeAndFlush(new DefaultHttpContent(
                Unpooled.copiedBuffer(text, StandardCharsets.UTF_8)));
    }

    @Override
    public synchronized void close() {
        if (!this.open) return;
        this.open = false;
        if (this.onCloseHandler != null) {
            this.onCloseHandler.run();
        }
    }

    @Override
    public SseChannel onClose(Runnable handler) {
        this.onCloseHandler = handler;
        return this;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }
}
