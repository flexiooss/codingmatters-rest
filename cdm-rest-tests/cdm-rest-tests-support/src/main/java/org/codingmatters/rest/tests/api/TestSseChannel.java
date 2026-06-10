package org.codingmatters.rest.tests.api;

import org.codingmatters.rest.api.SseChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TestSseChannel implements SseChannel {

    public record SseEvent(String event, String data) {}

    private final LinkedBlockingQueue<SseEvent> events = new LinkedBlockingQueue<>();
    private volatile boolean open = true;
    private Runnable onCloseHandler;

    @Override
    public SseChannel send(String event, String data) {
        events.offer(new SseEvent(event, data));
        return this;
    }

    @Override
    public SseChannel send(String data) {
        events.offer(new SseEvent(null, data));
        return this;
    }

    @Override
    public SseChannel comment(String comment) {
        return this;
    }

    @Override
    public SseChannel id(String id) {
        return this;
    }

    @Override
    public SseChannel retry(long milliseconds) {
        return this;
    }

    @Override
    public void close() {
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

    public SseEvent poll(long timeoutMs) throws InterruptedException {
        return events.poll(timeoutMs, TimeUnit.MILLISECONDS);
    }

    public List<SseEvent> drainEvents() {
        List<SseEvent> result = new ArrayList<>();
        events.drainTo(result);
        return result;
    }
}
