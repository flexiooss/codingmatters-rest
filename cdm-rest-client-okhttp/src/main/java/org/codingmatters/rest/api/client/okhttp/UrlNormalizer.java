package org.codingmatters.rest.api.client.okhttp;

public class UrlNormalizer {
    private String baseUrl;
    private String path;

    public UrlNormalizer(String baseUrl, String path) {
        this.baseUrl = baseUrl;
        this.path = path;
    }

    public String normalize() {
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.isEmpty()) {
            return baseUrl;
        } else {
            return baseUrl + "/" + path;
        }
    }
}
