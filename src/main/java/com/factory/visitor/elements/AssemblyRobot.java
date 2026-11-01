package com.factory.visitor.elements;

import com.factory.visitor.FactoryElement;
import com.factory.visitor.FactoryVisitor;

public final class AssemblyRobot implements FactoryElement {
    private final String id;
    private final double hourlyDowntimeCost;
    private final boolean active;

    public AssemblyRobot(String id, double hourlyDowntimeCost, boolean active) {
        this.id = id;
        this.hourlyDowntimeCost = hourlyDowntimeCost;
        this.active = active;
    }

    @Override
    public String getId() {
        return id;
    }

    public double getHourlyDowntimeCost() {
        return hourlyDowntimeCost;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public <R> R accept(FactoryVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
