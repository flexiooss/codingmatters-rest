package org.codingmatters.rest.api.generator.utils;

import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.ValueSpec;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnnotationProcessor {

    public void appendConformsToAnnotations(ValueSpec.Builder valueSpec, List<AnnotationRef> annotations) {
        for (AnnotationRef annotation : annotations) {
            if (annotation.name().equalsIgnoreCase("(conforms-to)")) {
                if(annotation.structuredValue().properties().get(0) != null
                        && annotation.structuredValue().properties().get(0).isArray()) {
                    for (TypeInstance typeInstance : annotation.structuredValue().properties().get(0).values()) {
                        valueSpec.addConformsTo(typeInstance.value().toString());
                    }
                }
            }
        }
    }

    public void appendValueObjectHints(PropertySpec.Builder prop, List<AnnotationRef> annotations) {
        Set<String> hints = new HashSet<>();
        for (AnnotationRef annotation : annotations) {
            if(annotation.name().equalsIgnoreCase("(value-object-hint)")) {
                if(annotation.structuredValue().properties().get(0) != null
                        && annotation.structuredValue().properties().get(0).isArray()) {
                    for (TypeInstance typeInstance : annotation.structuredValue().properties().get(0).values()) {
                        hints.add(typeInstance.value().toString());
                    }
                }
            }
        }
        if(! hints.isEmpty()) {
            prop.hints(hints);
        }

    }
}
