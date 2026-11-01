package com.factory.visitor.elements;

import com.factory.visitor.FactoryElement;
import com.factory.visitor.FactoryVisitor;

public final class ThermalSensorUnit implements FactoryElement {
    private final String id;
    private final double hourlyDowntimeCost;

    public ThermalSensorUnit(String id, double hourlyDowntimeCost) {
        this.id = id;
        this.hourlyDowntimeCost = hourlyDowntimeCost;
    }

    @Override
    public String getId() {
        return id;
    }

    public double getHourlyDowntimeCost() {
        return hourlyDowntimeCost;
    }

    @Override
    public <R> R accept(FactoryVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
