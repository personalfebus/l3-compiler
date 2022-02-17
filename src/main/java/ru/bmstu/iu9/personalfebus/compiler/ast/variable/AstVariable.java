package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;

public class AstVariable {
    private final String name;
    private AstType type;
    private RValue rValue;

    public AstVariable(String name) {
        this.name = name;
    }

    public AstVariable(String name, RValue rValue) {
        this.name = name;
        this.rValue = rValue;
    }

    public boolean isInitialized() {
        return rValue != null;
    }

    public String getName() {
        return name;
    }

    public AstType getType() {
        return type;
    }

    public RValue getrValue() {
        return rValue;
    }

    public void setType(AstType type) {
        this.type = type;
    }

    public void setrValue(RValue rValue) {
        this.rValue = rValue;
    }
}
