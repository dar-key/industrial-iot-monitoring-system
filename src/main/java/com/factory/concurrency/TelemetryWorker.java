package com.factory.concurrency;

import com.factory.observer.MachineTelemetryEvent;
import com.factory.observer.Subject;
import java.util.concurrent.CountDownLatch;

public class TelemetryWorker implements Runnable {
    private final Subject subject;
    private final String prefix;
    private final int count;
    private final CountDownLatch latch;

    public TelemetryWorker(Subject subject, String prefix, int count, CountDownLatch latch) {
        this.subject = subject;
        this.prefix = prefix;
        this.count = count;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < count; i++) {
                subject.publish(
                        new MachineTelemetryEvent(prefix + "-" + i, 65.0, 2.5, 120.0, System.currentTimeMillis()));
            }
        } finally {
            latch.countDown();
        }
    }
}
