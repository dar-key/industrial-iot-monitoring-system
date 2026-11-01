package com.factory.visitor.elements;

import com.factory.visitor.FactoryElement;
import com.factory.visitor.FactoryVisitor;

public final class CncStation implements FactoryElement {
    private final String id;
    private final double hourlyDowntimeCost;
    private final boolean calibrated;

    public CncStation(String id, double hourlyDowntimeCost, boolean calibrated) {
        this.id = id;
        this.hourlyDowntimeCost = hourlyDowntimeCost;
        this.calibrated = calibrated;
    }

    @Override
    public String getId() {
        return id;
    }

    public double getHourlyDowntimeCost() {
        return hourlyDowntimeCost;
    }

    public boolean isCalibrated() {
        return calibrated;
    }

    @Override
    public <R> R accept(FactoryVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
