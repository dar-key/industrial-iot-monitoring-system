package com.factory.observer.listeners;

import com.factory.observer.MachineTelemetryEvent;
import com.factory.observer.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class PredictiveMaintenanceTracker implements Observer<MachineTelemetryEvent> {
    private final AtomicInteger vibrationWarnings = new AtomicInteger(0);

    @Override
    public void onEvent(MachineTelemetryEvent event) {
        if (event.vibrationMmS() > 5.0)
            vibrationWarnings.incrementAndGet();
    }

    public int getVibrationWarnings() {
        return vibrationWarnings.get();
    }
}
