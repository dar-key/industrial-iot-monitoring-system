package com.factory.observer.listeners;

import com.factory.observer.MachineAlertEvent;
import com.factory.observer.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class EmergencyStopSystem implements Observer<MachineAlertEvent> {
    private final AtomicInteger triggeredEStops = new AtomicInteger(0);

    @Override
    public void onEvent(MachineAlertEvent event) {
        if (event.isCritical())
            triggeredEStops.incrementAndGet();
    }

    public int getTriggeredEStops() {
        return triggeredEStops.get();
    }
}
