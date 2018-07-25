package org.codingmatters.rest.php.api.client.model;

import java.util.ArrayList;
import java.util.List;

public class ResourceClientDescriptor {

    private final String className;
    private final List<HttpMethodDescriptor> methodDescriptors;
    private final List<ResourceClientDescriptor> nextFloorResourceClientGetters;
    private final String classPackage;

    public ResourceClientDescriptor( String className, String classPackage ) {
        this.className = className;
        this.classPackage = classPackage;
        this.methodDescriptors = new ArrayList<>();
        this.nextFloorResourceClientGetters = new ArrayList<>();
    }

    public void addNextFloorResource( ResourceClientDescriptor nextFloorResource ) {
        this.nextFloorResourceClientGetters.add( nextFloorResource );
    }

    public void addMethodDescriptor( HttpMethodDescriptor httpMethod ) {
        this.methodDescriptors.add( httpMethod );
    }

    public String getClassName() {
        return className;
    }

    public List<HttpMethodDescriptor> methodDescriptors() {
        return methodDescriptors;
    }

    public List<ResourceClientDescriptor> nextFloorResourceClientGetters() {
        return nextFloorResourceClientGetters;
    }
}
