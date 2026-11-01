package com.factory.visitor.elements;

import com.factory.visitor.FactoryElement;
import com.factory.visitor.FactoryVisitor;
import java.util.List;

public final class PlantFactory implements FactoryElement {
    private final String id;
    private final List<FactoryElement> children;

    public PlantFactory(String id, List<FactoryElement> children) {
        this.id = id;
        this.children = List.copyOf(children);
    }

    @Override
    public String getId() {
        return id;
    }

    public List<FactoryElement> getChildren() {
        return children;
    }

    @Override
    public <R> R accept(FactoryVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
