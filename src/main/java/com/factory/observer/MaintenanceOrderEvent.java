package com.factory.observer;

public record MaintenanceOrderEvent(
                String orderId,
                String targetLineId,
                String diagnosis,
                double estimatedDowntimeCost,
                long timestamp) implements Event {
}
