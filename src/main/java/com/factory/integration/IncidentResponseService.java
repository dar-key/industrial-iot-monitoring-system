package com.factory.integration;

import com.factory.observer.MachineAlertEvent;
import com.factory.observer.MaintenanceOrderEvent;
import com.factory.observer.Observer;
import com.factory.observer.Subject;
import com.factory.visitor.FactoryElement;
import com.factory.visitor.visitors.DowntimeCostVisitor;
import java.util.Map;
import java.util.UUID;

public class IncidentResponseService implements Observer<MachineAlertEvent> {
    private final Subject bus;
    private final Map<String, FactoryElement> lineRegistry;
    private final DowntimeCostVisitor costVisitor;

    public IncidentResponseService(Subject bus, Map<String, FactoryElement> lineRegistry,
            DowntimeCostVisitor costVisitor) {
        this.bus = bus;
        this.lineRegistry = Map.copyOf(lineRegistry);
        this.costVisitor = costVisitor;
    }

    @Override
    public void onEvent(MachineAlertEvent event) {
        if (!event.isCritical())
            return;
        FactoryElement line = lineRegistry.get(event.lineId());
        if (line != null) {
            double estimatedCost = line.accept(costVisitor);
            bus.publish(new MaintenanceOrderEvent(
                    UUID.randomUUID().toString(),
                    line.getId(),
                    "Emergency shutdown on machine: " + event.machineId() + " (" + event.alertType() + ")",
                    estimatedCost,
                    System.currentTimeMillis()));
        }
    }
}
