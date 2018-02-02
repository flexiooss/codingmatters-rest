package org.codingmatters.rest.api.processors;

import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;

import java.io.IOException;

public abstract class GuardedProcessor implements Processor {
    private final Processor guarded;

    public GuardedProcessor(Processor guarded) {
        this.guarded = guarded;
    }

    @Override
    public void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        if(this.passed(requestDelegate, responseDelegate)) {
            this.guarded.process(requestDelegate, responseDelegate);
        }
    }

    abstract protected boolean passed(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException;
}
