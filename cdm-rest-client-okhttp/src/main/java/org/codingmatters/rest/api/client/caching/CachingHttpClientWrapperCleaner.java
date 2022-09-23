package org.codingmatters.rest.api.client.caching;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CachingHttpClientWrapperCleaner {
    private final ScheduledExecutorService scheduler;
    private final List<CachingHttpClientWrapper> registered = Collections.synchronizedList(new LinkedList<>());
    private final long delay;
    private final long ttl;
    private ScheduledFuture<?> scheduled;

    public CachingHttpClientWrapperCleaner(ScheduledExecutorService scheduler, long delay, long ttl) {
        this.scheduler = scheduler;
        this.delay = delay;
        this.ttl = ttl;
    }

    public CachingHttpClientWrapper register(CachingHttpClientWrapper cachingWrapper) {
        this.registered.add(cachingWrapper);
        return cachingWrapper;
    }

    private void cleanup() {
        synchronized (this.registered) {
            for (CachingHttpClientWrapper cachingWrapper : this.registered) {
                cachingWrapper.cleanup(this.ttl);
            }
        }
    }

    public synchronized void start() {
        if(this.scheduled == null) {
            this.scheduled = this.scheduler.scheduleWithFixedDelay(this::cleanup, this.delay, this.delay, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void stop() {
        if(this.scheduled != null) {
            this.scheduled.cancel(false);
            this.scheduled = null;
        }
    }
}
