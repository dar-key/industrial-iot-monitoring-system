package com.factory.visitor;

public interface FactoryElement {
    String getId();

    <R> R accept(FactoryVisitor<R> visitor);
}
