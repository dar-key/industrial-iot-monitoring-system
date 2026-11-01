package com.factory.observer;

public record MachineTelemetryEvent(
                String machineId,
                double temperatureC,
                double vibrationMmS,
                double powerKw,
                long timestamp) implements Event {
}
