package org.codingmatters.rest.api.client.caching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CachingHttpClientWrapperCleaner {
    static private final Logger log = LoggerFactory.getLogger(CachingHttpClientWrapperCleaner.class);
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
        try {
            synchronized (this.registered) {
                for (CachingHttpClientWrapper cachingWrapper : this.registered) {
                    cachingWrapper.cleanup(this.ttl);
                }
            }
        } catch (Exception e) {
            log.error("error cleaning wrapped client cache", e);
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
