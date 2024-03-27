package org.codingmatters.rest.server.netty.explore;

import java.util.LinkedList;
import java.util.List;

public class ExploreMessage {
    private final List<String> lines;

    public ExploreMessage() {
        this.lines = new LinkedList<>();
    }

    public void append(String line) {
        this.lines.add(line);
    }

    @Override
    public String toString() {
        return "ExploreMessage{" +
                "lines count: " + lines.size() +
                '}';
    }
}
