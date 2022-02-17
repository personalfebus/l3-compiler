package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;

public class AstVariable {
    private final String name;
    private final String type;
    private RValue rValue;

    public AstVariable(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public AstVariable(String name, String type, RValue rValue) {
        this.name = name;
        this.type = type;
        this.rValue = rValue;
    }

    public boolean isInitialized() {
        return rValue != null;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public RValue getrValue() {
        return rValue;
    }
}
