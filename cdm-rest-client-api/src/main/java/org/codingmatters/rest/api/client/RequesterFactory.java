package org.codingmatters.rest.api.client;

public interface RequesterFactory {
    Requester forBaseUrl(String url);
}
