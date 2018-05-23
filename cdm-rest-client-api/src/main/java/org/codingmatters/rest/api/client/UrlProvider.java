package org.codingmatters.rest.api.client;

import java.io.IOException;

@FunctionalInterface
public interface UrlProvider {
    String baseUrl() throws IOException;
}
