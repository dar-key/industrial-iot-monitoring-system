package com.factory.visitor.visitors;

import com.factory.visitor.FactoryVisitor;
import com.factory.visitor.elements.*;

public class DowntimeCostVisitor implements FactoryVisitor<Double> {
    @Override
    public Double visit(PlantFactory f) {
        return f.getChildren().stream().mapToDouble(c -> c.accept(this)).sum();
    }

    @Override
    public Double visit(ProductionLine l) {
        return l.getChildren().stream().mapToDouble(c -> c.accept(this)).sum();
    }

    @Override
    public Double visit(AssemblyRobot r) {
        return r.getHourlyDowntimeCost() * 2.0;
    }

    @Override
    public Double visit(ConveyorBelt c) {
        return c.getHourlyDowntimeCost() + (c.getLengthMeters() * 1.5);
    }

    @Override
    public Double visit(CncStation s) {
        return s.getHourlyDowntimeCost() * 1.8;
    }

    @Override
    public Double visit(ThermalSensorUnit u) {
        return u.getHourlyDowntimeCost();
    }
}
