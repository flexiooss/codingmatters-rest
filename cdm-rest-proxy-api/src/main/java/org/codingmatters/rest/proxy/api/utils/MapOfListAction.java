package org.codingmatters.rest.proxy.api.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum MapOfListAction {
    PUT {
        @Override
        public void apply(String name, String[] values, Map<String, List<String>> to) {
            to.put(name, values != null ? Arrays.asList(values) : new LinkedList<>());
        }
    },
    ADD {
        @Override
        public void apply(String name, String[] values, Map<String, List<String>> to) {
            if(values == null || values.length == 0) return ;

            List<String> current = to.containsKey(name) ? new LinkedList<>(to.get(name)) : new LinkedList<>();
            for (String value : values) {
                current.add(value);
            }

            to.put(name, current);
        }
    },
    REMOVE {
        @Override
        public void apply(String name, String[] values, Map<String, List<String>> to) {
            to.remove(name);
        }
    };

    abstract public void apply(String name, String[] values, Map<String, List<String>> to);
}
