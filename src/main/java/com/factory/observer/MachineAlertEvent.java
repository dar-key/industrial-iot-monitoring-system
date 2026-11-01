package com.factory.observer;

public record MachineAlertEvent(
                String machineId,
                String lineId,
                String alertType,
                boolean isCritical,
                long timestamp) implements Event {
}
