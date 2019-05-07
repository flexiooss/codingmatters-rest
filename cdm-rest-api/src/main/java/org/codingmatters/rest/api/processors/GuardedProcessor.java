package org.codingmatters.rest.api.processors;

import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public abstract class GuardedProcessor implements Processor {
    static private final Logger log = LoggerFactory.getLogger(GuardedProcessor.class);
    private final Processor guarded;

    public GuardedProcessor(Processor guarded) {
        this.guarded = guarded;
    }

    @Override
    public void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        if(this.passed(requestDelegate, responseDelegate)) {
            this.guarded.process(requestDelegate, responseDelegate);
        } else {
            String token = UUID.randomUUID().toString();
            MDC.put("token", token);
            log.info("request refused by guard {}, {}", this.getClass().getName(), requestDelegate);
            MDC.remove("token");
            responseDelegate.status(403).contenType("application/json").payload(
                    String.format(
                            "{\"description\":\"%s\",\"token\":\"%s\"}",
                            "request refused by guard, see logs with token.", token
                    ).getBytes()
            );
        }
    }

    abstract protected boolean passed(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException;
}
