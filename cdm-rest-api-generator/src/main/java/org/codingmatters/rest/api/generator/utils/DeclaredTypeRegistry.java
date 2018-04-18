package org.codingmatters.rest.api.generator.utils;

import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.HashMap;
import java.util.Map;

public class DeclaredTypeRegistry {

    static private class Registry {
        private boolean initialized = false;
        private final Map<String, TypeDeclaration> store = new HashMap<>();

        public boolean initialized() {
            return initialized;
        }

        public void setInitialized(boolean initialized) {
            this.initialized = initialized;
        }

        public Map<String, TypeDeclaration> store() {
            return store;
        }
    }

    static private ThreadLocal<Registry> typesPerThread = new ThreadLocal<Registry>() {
        @Override
        protected Registry initialValue() {
            return new Registry();
        }
    };

    static public Map<String, TypeDeclaration> declaredTypes() {
        if(! typesPerThread.get().initialized()) throw new RuntimeException("DeclaredTypeRegistry is not initialized");
        return typesPerThread.get().store();
    }

    static public void initialize(RamlModelResult ramlModel) {
        for (TypeDeclaration type : ramlModel.getApiV10().types()) {
            typesPerThread.get().store().put(type.name(), type);
        }
        typesPerThread.get().setInitialized(true);
    }

}
