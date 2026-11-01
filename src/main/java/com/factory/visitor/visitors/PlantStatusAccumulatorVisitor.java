package com.factory.visitor.visitors;

import com.factory.visitor.FactoryElement;
import com.factory.visitor.FactoryVisitor;
import com.factory.visitor.elements.*;

public class PlantStatusAccumulatorVisitor implements FactoryVisitor<PlantStatusAccumulatorVisitor.PlantSummary> {
    public record PlantSummary(int totalMachines, int activeRobots, int sensorUnits) {
        public PlantSummary merge(PlantSummary o) {
            return new PlantSummary(this.totalMachines + o.totalMachines, this.activeRobots + o.activeRobots,
                    this.sensorUnits + o.sensorUnits);
        }
    }

    @Override
    public PlantSummary visit(PlantFactory f) {
        return aggregate(f.getChildren());
    }

    @Override
    public PlantSummary visit(ProductionLine l) {
        return aggregate(l.getChildren());
    }

    @Override
    public PlantSummary visit(AssemblyRobot r) {
        return new PlantSummary(1, r.isActive() ? 1 : 0, 0);
    }

    @Override
    public PlantSummary visit(ConveyorBelt c) {
        return new PlantSummary(1, 0, 0);
    }

    @Override
    public PlantSummary visit(CncStation s) {
        return new PlantSummary(1, 0, 0);
    }

    @Override
    public PlantSummary visit(ThermalSensorUnit u) {
        return new PlantSummary(1, 0, 1);
    }

    @Override
    public PlantSummary visit(AgvForklift agv) {
        return new PlantSummary(1, 0, 0);
    }

    private PlantSummary aggregate(Iterable<FactoryElement> children) {
        PlantSummary total = new PlantSummary(0, 0, 0);
        for (FactoryElement e : children)
            total = total.merge(e.accept(this));
        return total;
    }
}
