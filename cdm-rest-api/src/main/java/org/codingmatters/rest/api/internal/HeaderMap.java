package org.codingmatters.rest.api.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HeaderMap extends HashMap<String, List<String>> {
    @Override
    public List<String> get(Object key) {
        return super.get(this.normalizedObject(key));
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return super.put(this.normalizedString(key), value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {
        m.forEach((k, v) -> this.put(k, v));
    }

    @Override
    public List<String> remove(Object key) {
        return super.remove(this.normalizedObject(key));
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(this.normalizedObject(key));
    }

    @Override
    public List<String> getOrDefault(Object key, List<String> defaultValue) {
        return super.getOrDefault(this.normalizedObject(key), defaultValue);
    }

    @Override
    public List<String> putIfAbsent(String key, List<String> value) {
        return super.putIfAbsent(this.normalizedString(key), value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return super.remove(this.normalizedObject(key), value);
    }

    @Override
    public boolean replace(String key, List<String> oldValue, List<String> newValue) {
        return super.replace(this.normalizedString(key), oldValue, newValue);
    }

    @Override
    public List<String> replace(String key, List<String> value) {
        return super.replace(this.normalizedString(key), value);
    }

    @Override
    public List<String> computeIfAbsent(String key, Function<? super String, ? extends List<String>> mappingFunction) {
        return super.computeIfAbsent(this.normalizedString(key), mappingFunction);
    }

    @Override
    public List<String> computeIfPresent(String key, BiFunction<? super String, ? super List<String>, ? extends List<String>> remappingFunction) {
        return super.computeIfPresent(this.normalizedString(key), remappingFunction);
    }

    @Override
    public List<String> compute(String key, BiFunction<? super String, ? super List<String>, ? extends List<String>> remappingFunction) {
        return super.compute(this.normalizedString(key), remappingFunction);
    }

    @Override
    public List<String> merge(String key, List<String> value, BiFunction<? super List<String>, ? super List<String>, ? extends List<String>> remappingFunction) {
        return super.merge(this.normalizedString(key), value, remappingFunction);
    }

    private Object normalizedObject(Object key) {
        if(key == null) return null;
        if(key instanceof String) {
            return this.normalizedString((String) key);
        } else {
            return key;
        }
    }

    private String normalizedString(String key) {
        if(key == null) return null;
        return key.toLowerCase();
    }


}
