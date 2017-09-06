package org.codingmatters.rest.undertow.support;

import io.undertow.server.HttpServerExchange;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BehaviouralUndertowResource extends ExternalResource {

    static private final Logger log = LoggerFactory.getLogger(BehaviouralUndertowResource.class);

    private UndertowResource undertow;
    private final List<BehaviourImpl> behaviours = new LinkedList<>();

    public BehaviouralUndertowResource() {
        this.undertow = new UndertowResource(this::handle);
    }


    public String baseUrl() {
        return this.undertow.baseUrl();
    }

    @Override
    public void before() throws Throwable {
        log.debug("initializing behavioural undertow resource");
        this.undertow.before();
        this.behaviours.clear();
    }

    @Override
    public void after() {
        this.undertow.after();
    }

    private void handle(HttpServerExchange exchange) {
        log.debug("handler with behaviours : {}", this.behaviours);
        log.debug("called with exchange : {} ; query parameters : {}", exchange, exchange.getQueryParameters());

        boolean oneApplied = false;
        for (BehaviourImpl behaviour : this.behaviours) {
            boolean applied = behaviour.apply(exchange);
            if(! oneApplied) {
                oneApplied = applied;
            }
        }

        if(! oneApplied) {
            exchange.setStatusCode(404);
        }
    }

    public Behaviour when(Predicate<HttpServerExchange> predicate) {
        BehaviourImpl result = new BehaviourImpl(this, predicate);
        return result;
    }

    public interface Behaviour {
        BehaviouralUndertowResource then(Consumer<HttpServerExchange> consumer);
    }

    private class BehaviourImpl implements Behaviour {
        private final BehaviouralUndertowResource undertowResource;
        private final Predicate<HttpServerExchange> predicate;
        private Consumer<HttpServerExchange> consumer;

        public BehaviourImpl(BehaviouralUndertowResource undertowResource, Predicate<HttpServerExchange> predicate) {
            this.undertowResource = undertowResource;
            this.predicate = predicate;
        }

        @Override
        public BehaviouralUndertowResource then(Consumer<HttpServerExchange> consumer) {
            this.consumer = consumer;
            this.undertowResource.behaviours.add(this);
            log.debug("behaviour added");
            return this.undertowResource;
        }

        public boolean apply(HttpServerExchange exchange) {
            if(this.predicate.test(exchange)) {
                this.consumer.accept(exchange);
                return true;
            }
            return false;
        }
    }
}
