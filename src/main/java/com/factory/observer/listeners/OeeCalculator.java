package com.factory.observer.listeners;

import com.factory.observer.MachineTelemetryEvent;
import com.factory.observer.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class OeeCalculator implements Observer<MachineTelemetryEvent> {
    private final AtomicInteger processed = new AtomicInteger(0);

    @Override
    public void onEvent(MachineTelemetryEvent event) {
        processed.incrementAndGet();
    }

    public int getProcessed() {
        return processed.get();
    }
}
