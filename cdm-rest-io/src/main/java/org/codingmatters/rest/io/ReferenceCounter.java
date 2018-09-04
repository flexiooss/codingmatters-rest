package org.codingmatters.rest.io;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class ReferenceCounter {
    private final File temporary;
    private final AtomicInteger count = new AtomicInteger();

    public ReferenceCounter(File temporary) {
        this.temporary = temporary;
    }

    public int count() {
        return count.get();
    }

    public void increment() {
        this.count.incrementAndGet();
    }

    public synchronized void decrement() {
        this.count.decrementAndGet();
        if(this.count.get() <= 0) {
            this.temporary.delete();
        }
    }
}
