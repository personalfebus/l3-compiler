package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

public class AstVariableInt implements AstVariable {
    private final String name;
    private final boolean isConstant;
    private boolean isInitialized;

    private int value;

    private static String TYPE = "INT";

    public AstVariableInt(String name) {
        this.name = name;
        isInitialized = false;
        isConstant = false;
    }

    public AstVariableInt(int value) {
        this.name = "";
        isConstant = true;
        this.value = value;
        isInitialized = true;
    }

    public AstVariableInt(String name, int value) {
        this.name = name;
        this.value = value;
        isInitialized = true;
        isConstant = false;
    }

    public void setValue(int value) {
        this.value = value;
        if (!isInitialized) {
            isInitialized = true;
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }
}
