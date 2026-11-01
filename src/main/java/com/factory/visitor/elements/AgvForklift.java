package com.factory.visitor.elements;

import com.factory.visitor.FactoryElement;
import com.factory.visitor.FactoryVisitor;

public final class AgvForklift implements FactoryElement {
    private final String id;

    public AgvForklift(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public <R> R accept(FactoryVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
