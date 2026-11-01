package com.factory.observer.listeners;

import com.factory.observer.MachineTelemetryEvent;
import com.factory.observer.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class ScadaDashboard implements Observer<MachineTelemetryEvent> {
    private final AtomicInteger telemetryCount = new AtomicInteger(0);

    @Override
    public void onEvent(MachineTelemetryEvent event) {
        telemetryCount.incrementAndGet();
    }

    public int getTelemetryCount() {
        return telemetryCount.get();
    }
}
