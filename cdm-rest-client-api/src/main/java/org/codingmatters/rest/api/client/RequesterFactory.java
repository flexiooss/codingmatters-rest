package org.codingmatters.rest.api.client;

@FunctionalInterface
public interface RequesterFactory {
    Requester create();
}
