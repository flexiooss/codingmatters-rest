package org.codingmatters.rest.api.processors;

import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;

import java.io.IOException;

public class ProcessorChain implements Processor {

    static public Builder chain(Processor first) {
        return new Builder(first);
    }

    private final Processor first;
    private final Processor andThen;

    static public class Builder {
        private final Processor first;

        public Builder(Processor processor) {
            this.first = processor;
        }

        public ProcessorChain then(Processor processor) {
            return new ProcessorChain(this.first, processor);
        }
    }

    public ProcessorChain(Processor first, Processor andThen) {
        this.first = first;
        this.andThen = andThen;
    }

    @Override
    public void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        this.first.process(requestDelegate, responseDelegate);
        this.andThen.process(requestDelegate, responseDelegate);
    }

    public ProcessorChain then(Processor processor) {
        return ProcessorChain.chain(this).then(processor);
    }
}
