package org.codingmatters.rest.php.api.client.model;

public class Payload {

    private final Type type;
    private final String typeRef;

    public Payload( Type type, String typeRef ) {
        this.type = type;
        this.typeRef = typeRef;
    }

    public enum Type {
        VALUE_OBJECT,
        FILE;
    }
}
