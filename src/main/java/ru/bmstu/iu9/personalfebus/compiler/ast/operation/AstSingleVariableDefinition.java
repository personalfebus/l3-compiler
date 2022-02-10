package ru.bmstu.iu9.personalfebus.compiler.ast.operation;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;

public class AstSingleVariableDefinition {
    private String name;
    private boolean isInitialized;
    private RValue value;

    public AstSingleVariableDefinition(String name, boolean isInitialized, RValue value) {
        this.name = name;
        this.isInitialized = isInitialized;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public RValue getValue() {
        return value;
    }
}
