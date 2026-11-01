package com.factory.app;

import com.factory.integration.IncidentResponseService;
import com.factory.observer.LineMetricsAggregator;
import com.factory.observer.MachineAlertEvent;
import com.factory.observer.MachineTelemetryEvent;
import com.factory.observer.Subject;
import com.factory.observer.listeners.*;
import com.factory.visitor.elements.*;
import com.factory.visitor.visitors.DowntimeCostVisitor;
import java.util.List;
import java.util.Map;

public class CompositionRoot {
    public static Subject bootstrap() {
        Subject bus = new Subject();
        LineMetricsAggregator agg = new LineMetricsAggregator();
        bus.register(MachineTelemetryEvent.class, agg);
        bus.register(MachineTelemetryEvent.class, new ScadaDashboard());
        bus.register(MachineTelemetryEvent.class, new PredictiveMaintenanceTracker());
        bus.register(MachineTelemetryEvent.class, new OeeCalculator());
        bus.register(MachineAlertEvent.class, new EmergencyStopSystem());

        AssemblyRobot r = new AssemblyRobot("ROBOT-1", 100.0, true);
        ConveyorBelt c = new ConveyorBelt("BELT-1", 50.0, 20.0);
        ProductionLine lineA = new ProductionLine("LINE-A", List.of(r, c));

        IncidentResponseService service = new IncidentResponseService(bus, Map.of("LINE-A", lineA),
                new DowntimeCostVisitor());
        bus.register(MachineAlertEvent.class, service);
        return bus;
    }
}
