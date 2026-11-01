package com.factory.visitor.visitors;

import com.factory.visitor.FactoryElement;
import com.factory.visitor.FactoryVisitor;
import com.factory.visitor.elements.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OperationalFilterVisitor implements FactoryVisitor<List<FactoryElement>> {
    @Override
    public List<FactoryElement> visit(PlantFactory f) {
        return collect(f.getChildren());
    }

    @Override
    public List<FactoryElement> visit(ProductionLine l) {
        return collect(l.getChildren());
    }

    @Override
    public List<FactoryElement> visit(AssemblyRobot r) {
        return r.isActive() ? List.of(r) : Collections.emptyList();
    }

    @Override
    public List<FactoryElement> visit(ConveyorBelt c) {
        return List.of(c);
    }

    @Override
    public List<FactoryElement> visit(CncStation s) {
        return s.isCalibrated() ? List.of(s) : Collections.emptyList();
    }

    @Override
    public List<FactoryElement> visit(ThermalSensorUnit u) {
        return List.of(u);
    }

    private List<FactoryElement> collect(Iterable<FactoryElement> children) {
        List<FactoryElement> result = new ArrayList<>();
        children.forEach(c -> result.addAll(c.accept(this)));
        return result;
    }
}
