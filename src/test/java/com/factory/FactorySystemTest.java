package com.factory;

import com.factory.concurrency.TelemetryWorker;
import com.factory.integration.IncidentResponseService;
import com.factory.observer.*;
import com.factory.observer.listeners.*;
import com.factory.visitor.FactoryElement;
import com.factory.visitor.elements.*;
import com.factory.visitor.visitors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FactorySystemTest {
    private Subject subject;
    private PlantFactory plant;
    private ProductionLine lineA;
    private AssemblyRobot robot1;
    private ConveyorBelt conveyor1;
    private CncStation cnc1;
    private ThermalSensorUnit sensor1;

    @BeforeEach
    void setUp() {
        subject = new Subject();
        robot1 = new AssemblyRobot("ROBOT-1", 100.0, true);
        conveyor1 = new ConveyorBelt("BELT-1", 50.0, 20.0);
        cnc1 = new CncStation("CNC-1", 80.0, true);
        sensor1 = new ThermalSensorUnit("SENSOR-1", 20.0);

        lineA = new ProductionLine("LINE-A", List.of(robot1, conveyor1, cnc1, sensor1));
        plant = new PlantFactory("PLANT-1", List.of(lineA));
    }

    @Test
    void testObserverReceivesEvent() {
        ScadaDashboard scada = new ScadaDashboard();
        subject.register(MachineTelemetryEvent.class, scada);
        subject.publish(new MachineTelemetryEvent("M1", 40, 1, 10, 0));
        assertEquals(1, scada.getTelemetryCount());
    }

    @Test
    void testMultipleSubscribersNotified() {
        ScadaDashboard scada = new ScadaDashboard();
        OeeCalculator oee = new OeeCalculator();
        subject.register(MachineTelemetryEvent.class, scada);
        subject.register(MachineTelemetryEvent.class, oee);
        subject.publish(new MachineTelemetryEvent("M1", 40, 1, 10, 0));
        assertEquals(1, scada.getTelemetryCount());
        assertEquals(1, oee.getProcessed());
    }

    @Test
    void testUnsubscribeInsideHandler() {
        AtomicInteger count = new AtomicInteger(0);
        Observer<MachineTelemetryEvent> selfUnsub = new Observer<>() {
            @Override
            public void onEvent(MachineTelemetryEvent event) {
                count.incrementAndGet();
                subject.unregister(MachineTelemetryEvent.class, this);
            }
        };
        subject.register(MachineTelemetryEvent.class, selfUnsub);
        subject.publish(new MachineTelemetryEvent("M1", 10, 1, 10, 0));
        subject.publish(new MachineTelemetryEvent("M2", 10, 1, 10, 0));
        assertEquals(1, count.get());
    }

    @Test
    void testAggregatorRepublishes() {
        LineMetricsAggregator agg = new LineMetricsAggregator();
        AtomicReference<LineMetricsAggregator.FactoryEnergySummaryEvent> ref = new AtomicReference<>();
        agg.register(LineMetricsAggregator.FactoryEnergySummaryEvent.class, ref::set);
        agg.onEvent(new MachineTelemetryEvent("M1", 0, 0, 100.0, 0));
        agg.onEvent(new MachineTelemetryEvent("M2", 0, 0, 200.0, 0));
        assertNotNull(ref.get());
        assertEquals(2, ref.get().totalReadings());
        assertEquals(150.0, ref.get().averagePowerKw(), 0.01);
    }

    @Test
    void testPredictiveMaintenanceThreshold() {
        PredictiveMaintenanceTracker tracker = new PredictiveMaintenanceTracker();
        subject.register(MachineTelemetryEvent.class, tracker);
        subject.publish(new MachineTelemetryEvent("M1", 20, 2.0, 10, 0));
        subject.publish(new MachineTelemetryEvent("M2", 20, 8.5, 10, 0));
        assertEquals(1, tracker.getVibrationWarnings());
    }

    @Test
    void testEmergencyStopTrigger() {
        EmergencyStopSystem estop = new EmergencyStopSystem();
        subject.register(MachineAlertEvent.class, estop);
        subject.publish(new MachineAlertEvent("M1", "LINE-A", "OVERHEAT", false, 0));
        subject.publish(new MachineAlertEvent("M2", "LINE-A", "FIRE", true, 0));
        assertEquals(1, estop.getTriggeredEStops());
    }

    @Test
    void testDowntimeCostHandCalculated() {
        // robot: 100*2 = 200
        // conveyor: 50 + (20*1.5) = 80
        // cnc: 80 * 1.8 = 144
        // sensor: 20
        // Total = 444.0
        double cost = plant.accept(new DowntimeCostVisitor());
        assertEquals(444.0, cost, 0.001);
    }

    @Test
    void testPlantStatusAccumulator() {
        PlantStatusAccumulatorVisitor.PlantSummary s = plant.accept(new PlantStatusAccumulatorVisitor());
        assertEquals(4, s.totalMachines());
        assertEquals(1, s.activeRobots());
        assertEquals(1, s.sensorUnits());
    }

    @Test
    void testOperationalFilter() {
        List<FactoryElement> ops = plant.accept(new OperationalFilterVisitor());
        assertEquals(4, ops.size());
    }

    @Test
    void testJsonExportCorrectStructure() {
        String json = plant.accept(new JsonExportVisitor());
        assertTrue(json.contains("\"type\": \"PlantFactory\""));
        assertTrue(json.contains("\"type\": \"AssemblyRobot\""));
        assertTrue(json.contains("\"id\": \"ROBOT-1\""));
    }

    @Test
    void testSubtreeVisitorExecution() {
        double lineCost = lineA.accept(new DowntimeCostVisitor());
        assertEquals(444.0, lineCost, 0.001);
    }

    @Test
    void testIncidentTriggersSubtreeInspectionAndIssuesTicket() {
        AtomicReference<MaintenanceOrderEvent> ticket = new AtomicReference<>();
        subject.register(MaintenanceOrderEvent.class, ticket::set);
        IncidentResponseService service = new IncidentResponseService(subject, Map.of("LINE-A", lineA),
                new DowntimeCostVisitor());
        subject.register(MachineAlertEvent.class, service);

        subject.publish(new MachineAlertEvent("ROBOT-1", "LINE-A", "GEARBOX_FAILURE", true, 0));
        assertNotNull(ticket.get());
        assertEquals("LINE-A", ticket.get().targetLineId());
        assertEquals(444.0, ticket.get().estimatedDowntimeCost(), 0.001);
    }

    @Test
    void testNonCriticalIncidentIgnored() {
        AtomicReference<MaintenanceOrderEvent> ticket = new AtomicReference<>();
        subject.register(MaintenanceOrderEvent.class, ticket::set);
        IncidentResponseService service = new IncidentResponseService(subject, Map.of("LINE-A", lineA),
                new DowntimeCostVisitor());
        subject.register(MachineAlertEvent.class, service);

        subject.publish(new MachineAlertEvent("ROBOT-1", "LINE-A", "LOW_OIL", false, 0));
        assertNull(ticket.get());
    }

    @Test
    void testStressTest4Threads1000EventsExactCount() throws InterruptedException {
        int threads = 4;
        int countPerThread = 1000;
        AtomicInteger received = new AtomicInteger(0);
        subject.register(MachineTelemetryEvent.class, e -> received.incrementAndGet());

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(new TelemetryWorker(subject, "T" + i, countPerThread, latch));
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        assertEquals(4000, received.get());
    }

    @Test
    void testConcurrentSubscribeAndPublishNoDeadlock() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(4);
        for (int i = 0; i < 4; i++) {
            pool.submit(() -> {
                Observer<MachineTelemetryEvent> obs = e -> {
                };
                subject.register(MachineTelemetryEvent.class, obs);
                for (int j = 0; j < 500; j++) {
                    subject.publish(new MachineTelemetryEvent("M", 50, 1, 10, 0));
                }
                subject.unregister(MachineTelemetryEvent.class, obs);
                latch.countDown();
            });
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
    }
}
