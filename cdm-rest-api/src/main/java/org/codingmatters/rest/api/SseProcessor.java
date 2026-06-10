package org.codingmatters.rest.api;

import java.io.IOException;

public interface SseProcessor {
    void process(RequestDelegate request, SseChannel channel) throws IOException;

    static Processor asProcessor(SseProcessor sseProcessor) {
        return (req, res) -> sseProcessor.process(req, res.openSse());
    }
}
