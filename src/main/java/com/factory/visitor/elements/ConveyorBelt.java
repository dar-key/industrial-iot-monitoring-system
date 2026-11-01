package com.factory.visitor.elements;

import com.factory.visitor.FactoryElement;
import com.factory.visitor.FactoryVisitor;

public final class ConveyorBelt implements FactoryElement {
    private final String id;
    private final double hourlyDowntimeCost;
    private final double lengthMeters;

    public ConveyorBelt(String id, double hourlyDowntimeCost, double lengthMeters) {
        this.id = id;
        this.hourlyDowntimeCost = hourlyDowntimeCost;
        this.lengthMeters = lengthMeters;
    }

    @Override
    public String getId() {
        return id;
    }

    public double getHourlyDowntimeCost() {
        return hourlyDowntimeCost;
    }

    public double getLengthMeters() {
        return lengthMeters;
    }

    @Override
    public <R> R accept(FactoryVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
