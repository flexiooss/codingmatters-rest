package org.codingmatters.rest.api.generator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.codingmatters.rest.api.generator.guards.descriptor.Guarded;
import org.codingmatters.rest.api.generator.guards.descriptor.json.GuardedWriter;
import org.codingmatters.value.objects.generation.Naming;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import static org.codingmatters.value.objects.generation.GenerationUtils.packageDir;

public class GuardsGenerator {
    private final File destinationDir;
    private final Naming naming;
    private final JsonFactory jsonFactory;

    public GuardsGenerator(String serverPackage, File rootDirectory, JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
        this.destinationDir = new File(rootDirectory, serverPackage.replaceAll("\\.", "/"));
        this.naming = new Naming();
    }

    public void generate(RamlModelResult raml) throws IOException {
        File guardsFile = new File(
                this.destinationDir,
                String.format("%s.guards", this.naming.apiName(raml.getApiV10().title().value()))
        );
        guardsFile.getParentFile().mkdirs();
        guardsFile.createNewFile();

        List<Guarded> result = this.appendResourcesGuards(raml.getApiV10().resources());
        try(FileOutputStream out = new FileOutputStream(guardsFile) ; JsonGenerator generator = this.jsonFactory.createGenerator(out)) {
            new GuardedWriter().writeArray(generator, result.toArray(new Guarded[0]));
        }
    }

    private List<Guarded> appendResourcesGuards(List<Resource> resources) {
        List<Guarded> result = new LinkedList<>();
        if(resources != null) {
            for (Resource resource : resources) {
                result.addAll(this.appendResourceGuards(resource));
            }
        }
        return result;
    }

    private List<Guarded> appendResourceGuards(Resource resource) {
        List<Guarded> result = new LinkedList<>();
        result.addAll(this.appendResourceMethodsGuards(resource.methods()));
        result.addAll(this.appendResourcesGuards(resource.resources()));
        return result;
    }

    private List<Guarded> appendResourceMethodsGuards(List<Method> methods) {
        List<Guarded> result = new LinkedList<>();
        if(methods != null) {
            for (Method method : methods) {
                result.addAll(this.appendResourceMethodGuards(method));
            }
        }
        return result;
    }

    private List<Guarded> appendResourceMethodGuards(Method method) {
        List<String> guards;
        if(this.isClearGuards(method)) {
            guards = this.guards(method);
        } else {
            guards = this.hierarchyGuards(method.resource());
            guards.addAll(this.guards(method));
        }

        if(! guards.isEmpty()) {
            Guarded guarded = Guarded.builder()
                    .path(method.resource().resourcePath())
                    .method(method.method())
                    .guards(guards)
                    .build();
            return List.of(guarded);
        } else {
            return List.of();
        }
    }

    private List<String> hierarchyGuards(Resource resource) {
        List<List<String>> hierarchyGuards = new LinkedList<>();
        while(resource != null) {
            hierarchyGuards.add(this.guards(resource));
            if(this.isClearGuards(resource)) {
                resource = null;
            } else {
                resource = resource.parentResource();
            }
        }

        List<String> result = new LinkedList<>();
        for (List<String> guards : hierarchyGuards.reversed()) {
            result.addAll(guards);
        }
        return result;
    }

    private List<String> guards(Annotable annotable) {
        List<String> result = new LinkedList<>();
        for (AnnotationRef annotation : annotable.annotations()) {
            if (annotation.name().equalsIgnoreCase("(guard)")) {
                if (annotation.structuredValue().properties().get(0) != null
                        && annotation.structuredValue().properties().get(0).isArray()) {
                    for (TypeInstance typeInstance : annotation.structuredValue().properties().get(0).values()) {
                        result.add(typeInstance.value().toString());
                    }
                }
            }
        }
        return result;
    }

    private boolean isClearGuards(Annotable annotable) {
        for (AnnotationRef annotation : annotable.annotations()) {
            if (annotation.name().equalsIgnoreCase("(clear-guards)")) {
                return true;
            }
        }
        return false;
    }
}
