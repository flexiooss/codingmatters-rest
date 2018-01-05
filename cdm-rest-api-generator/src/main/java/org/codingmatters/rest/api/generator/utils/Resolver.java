package org.codingmatters.rest.api.generator.utils;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Resolver {
    static public List<TypeDeclaration> resolvedUriParameters(Resource resource) {
        LinkedList<TypeDeclaration> result = new LinkedList<>();
        HashSet<String> paramsName = new HashSet<>();

        for(Resource current = resource ; current != null ; current = current.parentResource()) {
            for (TypeDeclaration typeDeclaration : current.uriParameters()) {
                if(! paramsName.contains(typeDeclaration.name())) {
                    result.add(typeDeclaration);
                    paramsName.add(typeDeclaration.name());
                }
            }
        }

        return result;
    }
}
