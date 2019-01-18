package org.codingmatters.rest.js.api.client;

public class PackagesConfiguration {
    private final String clientPackage;
    private final String apiPackage;
    private final String typesPackage;

    public PackagesConfiguration( String clientPackage, String apiPackage, String typesPackage ) {
        this.clientPackage = clientPackage;
        this.apiPackage = apiPackage;
        this.typesPackage = typesPackage;
    }

    public String clientPackage() {
        return clientPackage;
    }

    public String apiPackage() {
        return apiPackage;
    }

    public String typesPackage() {
        return typesPackage;
    }

}
