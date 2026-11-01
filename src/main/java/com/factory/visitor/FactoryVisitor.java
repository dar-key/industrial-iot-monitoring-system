package com.factory.visitor;

import com.factory.visitor.elements.*;

public interface FactoryVisitor<R> {
    R visit(PlantFactory factory);

    R visit(ProductionLine line);

    R visit(AssemblyRobot robot);

    R visit(ConveyorBelt conveyor);

    R visit(CncStation cnc);

    R visit(ThermalSensorUnit sensor);

    R visit(AgvForklift agv);
}
