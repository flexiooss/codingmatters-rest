package org.codingmatters.rest.api.internal;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * The version of an api : the release, or, for a SNAPSHOT (or without version), the time its class was built. The
 * time of the build, not of the start : the processes running the same build (instances of a service, a service and
 * its job runners) have the same version.
 */
public final class ApiVersion {

    private ApiVersion() {
    }

    static public String version(String raw, long built) {
        if (raw == null) {
            return "" + built;
        }
        if (raw.endsWith("-SNAPSHOT")) {
            return raw.replace("-SNAPSHOT", "." + built);
        }
        return raw;
    }

    /**
     * The time the class was built (its entry in the jar, its file in a classes directory) ; the current time when
     * unknown.
     */
    static public long built(Class<?> clazz) {
        URL resource = clazz.getResource(clazz.getSimpleName() + ".class");
        if (resource != null) {
            try {
                URLConnection connection = resource.openConnection();
                connection.setUseCaches(false);
                long modified = connection.getLastModified();
                if (modified > 0) {
                    return modified;
                }
            } catch (IOException e) {
                // unknown
            }
        }
        return System.currentTimeMillis();
    }
}
