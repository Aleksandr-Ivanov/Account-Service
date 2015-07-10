package net.ivanov.accountservice.server;

import java.util.concurrent.atomic.AtomicLong;

public class StatsHandler {
    private static volatile AtomicLong getQueries = new AtomicLong(0);
    private static volatile AtomicLong addQueries = new AtomicLong(0);
    private static volatile long startMillis = System.currentTimeMillis();

    public long getRequestRate(long quantity) {
        double elapsedMillis = System.currentTimeMillis() - startMillis;
        double total = quantity;
        
        double requestRate = 1000 * total / elapsedMillis;

        return (long) requestRate;
    }

    public void incrementGetStats() {
        getQueries.incrementAndGet();
    }

    public void incrementAddStats() {
        addQueries.incrementAndGet();
    }

    public void reset() {
        getQueries.set(0);
        addQueries.set(0);
        startMillis = System.currentTimeMillis();
    }

    public long getQuantityOfGet() {
        return getQueries.longValue();
    }

    public long getQuantityOfAdd() {
        return addQueries.longValue();
    }
}
