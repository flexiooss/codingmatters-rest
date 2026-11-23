package org.codingmatters.rest.api;

public interface Api {
    String name();
    String version();

    Processor processor();
    default String docResource() {
        return null;
    }

    default String path() {
        return '/' + this.name();
    }

    default SseProcessor sseProcessor() {
        return null;
    }

    /**
     * The release, or, for a SNAPSHOT, the version with the time the class was built (the same for every process
     * running that build).
     */
    static String versionFrom(Class clazz) {
        return org.codingmatters.rest.api.internal.ApiVersion.version(clazz.getPackage().getImplementationVersion(), org.codingmatters.rest.api.internal.ApiVersion.built(clazz));
    }
}
