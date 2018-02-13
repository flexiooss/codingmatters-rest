package org.codingmatters.rest.api.processors;

import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MatchingPathProcessor implements Processor {

    static public Builder whenMatching(String pattern, Processor processor) {
        return new Builder().whenMatching(pattern, processor);
    }

    static public class Builder {
        private final LinkedHashMap<String, Processor> processors = new LinkedHashMap<>();

        public Builder whenMatching(String pattern, Processor processor) {
            this.processors.put(pattern, processor);
            return this;
        }

        public Processor whenNoMatch(Processor defaultProcessor) {
            return new MatchingPathProcessor(defaultProcessor, this.processors);
        }
    }

    private final Processor defaultProcessor;
    private final LinkedHashMap<String, Processor> processors;

    private MatchingPathProcessor(Processor defaultProcessor, LinkedHashMap<String, Processor> processors) {
        this.defaultProcessor = defaultProcessor;
        this.processors = processors;
    }

    @Override
    public void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        for (Map.Entry<String, Processor> processorEntry : this.processors.entrySet()) {
            if(requestDelegate.pathMatcher(processorEntry.getKey()).matches()) {
                processorEntry.getValue().process(requestDelegate, responseDelegate);
                return ;
            }
        }
        this.defaultProcessor.process(requestDelegate, responseDelegate);
    }
}
