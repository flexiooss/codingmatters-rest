package org.codingmatters.rest.api.client.caching.rules;

import okhttp3.Response;
import org.codingmatters.rest.api.client.caching.CachingRule;

import java.util.HashSet;
import java.util.Set;

public class ResponseStatus implements CachingRule {

    static public ResponseStatus status(int...statuses) {
        return new ResponseStatus(statuses);
    }

    static public ResponseStatus ok() {
        return new ResponseStatus(200);
    }

    static public ResponseStatus created() {
        return new ResponseStatus(201);
    }

    static public ResponseStatus okAndCreated() {
        return new ResponseStatus(200, 201);
    }

    private final Set<Integer> acceptedStatuses;

    private ResponseStatus(int ... statuses) {
        this.acceptedStatuses = new HashSet<>();
        if (statuses != null) {
            for (int status : statuses) {
                this.acceptedStatuses.add(status);
            }
        }
    }

    @Override
    public boolean matches(Response response) {
        return this.acceptedStatuses.contains(response.code());
    }
}
