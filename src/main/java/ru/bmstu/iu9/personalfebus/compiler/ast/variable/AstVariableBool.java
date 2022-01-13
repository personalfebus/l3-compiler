package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

public class AstVariableBool implements AstVariable {
    private final String name;
    private final boolean isConstant;
    private boolean isInitialized;

    private boolean value;

    private static String TYPE = "BOOL";

    public AstVariableBool(String name) {
        this.name = name;
        isConstant = false;
        isInitialized = false;
    }

    public AstVariableBool(boolean value) {
        this.value = value;
        this.name = "";
        isConstant = true;
        isInitialized = true;
    }

    public AstVariableBool(String name, boolean value) {
        this.name = name;
        this.value = value;
        isInitialized = true;
        isConstant = false;
    }

    public void setValue(boolean value) {
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
