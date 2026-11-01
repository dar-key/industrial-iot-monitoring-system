package com.factory.visitor.visitors;

import com.factory.visitor.FactoryVisitor;
import com.factory.visitor.elements.*;
import java.util.stream.Collectors;

public class JsonExportVisitor implements FactoryVisitor<String> {
    private final int level;

    public JsonExportVisitor() {
        this(0);
    }

    private JsonExportVisitor(int level) {
        this.level = level;
    }

    private String pad() {
        return "  ".repeat(level);
    }

    @Override
    public String visit(PlantFactory f) {
        JsonExportVisitor sub = new JsonExportVisitor(level + 1);
        String ch = f.getChildren().stream().map(c -> c.accept(sub)).collect(Collectors.joining(",\n"));
        return String.format(
                "%s{\n%s  \"type\": \"PlantFactory\",\n%s  \"id\": \"%s\",\n%s  \"children\": [\n%s\n%s  ]\n%s}", pad(),
                pad(), pad(), f.getId(), pad(), ch, pad(), pad());
    }

    @Override
    public String visit(ProductionLine l) {
        JsonExportVisitor sub = new JsonExportVisitor(level + 1);
        String ch = l.getChildren().stream().map(c -> c.accept(sub)).collect(Collectors.joining(",\n"));
        return String.format(
                "%s{\n%s  \"type\": \"ProductionLine\",\n%s  \"id\": \"%s\",\n%s  \"children\": [\n%s\n%s  ]\n%s}",
                pad(), pad(), pad(), l.getId(), pad(), ch, pad(), pad());
    }

    @Override
    public String visit(AssemblyRobot r) {
        return String.format("%s{\"type\": \"AssemblyRobot\", \"id\": \"%s\", \"active\": %b}", pad(), r.getId(),
                r.isActive());
    }

    @Override
    public String visit(ConveyorBelt c) {
        return String.format("%s{\"type\": \"ConveyorBelt\", \"id\": \"%s\", \"length\": %.1f}", pad(), c.getId(),
                c.getLengthMeters());
    }

    @Override
    public String visit(CncStation s) {
        return String.format("%s{\"type\": \"CncStation\", \"id\": \"%s\", \"calibrated\": %b}", pad(), s.getId(),
                s.isCalibrated());
    }

    @Override
    public String visit(ThermalSensorUnit u) {
        return String.format("%s{\"type\": \"ThermalSensorUnit\", \"id\": \"%s\"}", pad(), u.getId());
    }

    @Override
    public String visit(AgvForklift agv) {
        return String.format("%s{\"type\": \"AgvForklift\", \"id\": \"%s\"}", pad(), agv.getId());
    }
}
