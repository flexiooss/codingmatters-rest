package org.codingmatters.rest.api;

import java.io.IOException;

public interface SseChannel extends AutoCloseable {
    SseChannel send(String event, String data) throws IOException;
    SseChannel send(String data) throws IOException;
    SseChannel comment(String comment) throws IOException;
    SseChannel id(String id) throws IOException;
    SseChannel retry(long milliseconds) throws IOException;
    @Override
    void close();
    SseChannel onClose(Runnable handler);
    boolean isOpen();
}
