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

    static String versionFrom(Class clazz) {
        String rawVersion = clazz.getPackage().getImplementationVersion();
        if(rawVersion == null) {
            return "" + System.currentTimeMillis();
        } else {
            if(rawVersion.endsWith("-SNAPSHOT")) {
                return rawVersion.replace("-SNAPSHOT", "." + System.currentTimeMillis());
            } else {
                return rawVersion;
            }
        }
    }
}
