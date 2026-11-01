package com.factory.visitor.visitors;

import com.factory.visitor.FactoryVisitor;
import com.factory.visitor.elements.*;

public class CarbonTaxVisitor implements FactoryVisitor<Double> {
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
        return 15.0;
    }

    @Override
    public Double visit(ConveyorBelt c) {
        return c.getLengthMeters() * 0.5;
    }

    @Override
    public Double visit(CncStation s) {
        return 25.0;
    }

    @Override
    public Double visit(ThermalSensorUnit u) {
        return 1.0;
    }
}
