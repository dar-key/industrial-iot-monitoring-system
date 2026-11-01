package com.factory.observer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

public class LineMetricsAggregator extends Subject implements Observer<MachineTelemetryEvent> {
    private final AtomicInteger readingCount = new AtomicInteger(0);
    private final DoubleAdder totalPower = new DoubleAdder();

    public record FactoryEnergySummaryEvent(int totalReadings, double averagePowerKw, long timestamp) implements Event {
    }

    @Override
    public void onEvent(MachineTelemetryEvent event) {
        int count = readingCount.incrementAndGet();
        totalPower.add(event.powerKw());
        double avg = totalPower.doubleValue() / count;
        publish(new FactoryEnergySummaryEvent(count, avg, System.currentTimeMillis()));
    }

    public int getReadingCount() {
        return readingCount.get();
    }
}
