package org.codingmatters.rest.proxy.api.utils;

import java.util.List;
import java.util.Map;

public class MapOfListModification {
    private final MapOfListAction action;
    private final String name;
    private final String[] values;

    public MapOfListModification(MapOfListAction action, String name, String ... values) {
        this.action = action;
        this.name = name;
        this.values = values;
    }

    public void appy(Map<String, List<String>> to) {
        this.action.apply(name, values, to);
    }
}
