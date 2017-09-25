package org.codingmatters.rest.api.generator.client;

import com.squareup.javapoet.ClassName;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.system.types.StatusCodeString;

public class ResourceNaming extends Naming {
    private final String apiPackage;
    private final String clientPackage;

    public ResourceNaming(String apiPackage, String clientPackage) {
        this.apiPackage = apiPackage;
        this.clientPackage = clientPackage;
    }


    public String resourcePackage() {
        return this.clientPackage + ".resources";
    }

    public String resourceType(Resource resource) {
        return this.type(resource.displayName().value());
    }

    public String resourceDelegateName(Resource resource) {
        return this.property(resource.displayName().value(), "Delegate");
    }

    public ClassName resourceClientType(Resource resource) {
        return ClassName.get(this.resourcePackage(), this.type(resource.displayName().value(), "Client"));
    }

    public ClassName methodResponseType(Method method) {
        return ClassName.get(this.apiPackage, this.type(this.resourceType(method.resource()), method.method(), "Response"));
    }

    public ClassName methodRequestType(Method method) {
        return ClassName.get(this.apiPackage, this.type(this.resourceType(method.resource()), method.method(), "Request"));
    }

    public ClassName methodResponseStatusType(Method method, StatusCodeString status) {
        method.resource();
        method.method();

        String pack = this.apiPackage + "." + this.type(this.resourceType(method.resource()), method.method(), "Response").toLowerCase();
        return ClassName.get(pack, this.type("Status", status.value()));
    }
}
