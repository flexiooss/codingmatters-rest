package org.codingmatters.rest.api.generator.client;

import org.raml.v2.api.RamlModelResult;

public class ClientNamingHelper {
    static public String interfaceName(ResourceNaming naming, RamlModelResult model) {
        return naming.type(model.getApiV10().title().value() , "Client");
    }
    static public String requesterClassName(ResourceNaming naming, RamlModelResult model) {
        return naming.type(model.getApiV10().title().value() , "RequesterClient");
    }
    static public String handlersClassName(ResourceNaming naming, RamlModelResult model) {
        return naming.type(model.getApiV10().title().value() , "HandlersClient");
    }
}
