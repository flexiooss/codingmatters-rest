package org.codingmatters.rest.api.generator.utils;

import com.squareup.javapoet.ClassName;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import java.util.LinkedList;
import java.util.function.Function;

/**
 * Created by nelt on 5/6/17.
 */
public class Naming {
    public String type(String ... parts) {
        return this.name(this::upperCaseFirst, parts);
    }

    public String property(String ... parts) {
        return this.name(this::lowerCaseFirst, parts);
    }

    private String name(Function<String, String> firstPartTransformer,  String ... parts) {
        if(parts == null) return null;
        if(parts.length == 0) return "";
        parts = this.normalize(parts);

        StringBuilder result = new StringBuilder(firstPartTransformer.apply(parts[0]));
        for(int i = 1 ; i < parts.length ; i++) {
            result.append(this.upperCaseFirst(parts[i]));
        }
        return result.toString();
    }

    private final String [] normalize(String ... parts) {
        LinkedList<String> result = new LinkedList<>();
        for (String part : parts) {
            for (String subpart : part.split("(\\s+)|(-+)")) {
                result.add(subpart);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    private String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    private String lowerCaseFirst(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public boolean isArbitraryObject(TypeDeclaration typeDeclaration) {
        return typeDeclaration.type().equals("object") &&
                ((ObjectTypeDeclaration)typeDeclaration).properties().isEmpty();
    }

    public boolean isArbitraryObjectArray(TypeDeclaration typeDeclaration) {
        if(typeDeclaration instanceof ArrayTypeDeclaration) {
            ArrayTypeDeclaration arrayDeclaration = (ArrayTypeDeclaration) typeDeclaration;
            System.out.println(arrayDeclaration.items().type() + "-" + arrayDeclaration.name() + "-" + arrayDeclaration.annotations());
            if(arrayDeclaration.items().type() == null) {
                return "object[]".equals(arrayDeclaration.items().name());
            }
            return "object".equals(arrayDeclaration.items().type()) &&
                    ((ObjectTypeDeclaration)arrayDeclaration.items()).properties().isEmpty();
        } else {
            return false;
        }
    }

    public  boolean isAlreadyDefined(TypeDeclaration typeDeclaration) {
        if(typeDeclaration.annotations() == null) return false;
        for (AnnotationRef annotation : typeDeclaration.annotations()) {
            if (annotation.name().equalsIgnoreCase("(already-defined)")) {
                return true;
            }
        }
        return false;
    }

    public String alreadyDefined(TypeDeclaration typeDeclaration) {
        if(typeDeclaration.annotations() == null) return null;
        for (AnnotationRef annotation : typeDeclaration.annotations()) {
            if (annotation.name().equalsIgnoreCase("(already-defined)")) {
                return annotation.structuredValue().value().toString();
            }
        }
        return null;
    }

    public ClassName alreadyDefinedClass(TypeDeclaration typeDeclaration) {
        String alreadyDefined = this.alreadyDefined(typeDeclaration);
        System.out.println("ALREADY : " + alreadyDefined + " from : name=" + typeDeclaration.name() + " type=" + typeDeclaration.type());
        String packageName = alreadyDefined.substring(0, alreadyDefined.lastIndexOf("."));
        String className = alreadyDefined.substring(alreadyDefined.lastIndexOf(".") + 1);
        return ClassName.get(packageName, this.type(className));
    }

    public ClassName alreadyDefinedWriter(TypeDeclaration typeDeclaration) {
        String alreadyDefined = this.alreadyDefined(typeDeclaration);
        String packageName = alreadyDefined.substring(0, alreadyDefined.lastIndexOf("."));
        String className = alreadyDefined.substring(alreadyDefined.lastIndexOf(".") + 1);
        return ClassName.get(packageName + ".json", this.type(className, "Writer"));
    }

    public ClassName alreadyDefinedReader(TypeDeclaration typeDeclaration) {
        String alreadyDefined = this.alreadyDefined(typeDeclaration);
        String packageName = alreadyDefined.substring(0, alreadyDefined.lastIndexOf("."));
        String className = alreadyDefined.substring(alreadyDefined.lastIndexOf(".") + 1);
        return ClassName.get(packageName + ".json", this.type(className, "Reader"));
    }

    public boolean isArray(TypeDeclaration type) {
        if(type.type().endsWith("[]")) return true;
        if(type instanceof ArrayTypeDeclaration) return true;

        return false;
    }
}
