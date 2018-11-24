package org.codingmatters.rest.api.client;

public interface RequesterFactory {
    @Deprecated
    Requester forBaseUrl(String url);
    Requester create();
}
