package ru.bmstu.iu9.personalfebus.compiler.ast;

public class AstVariableInt implements AstVariable {
    private int value;
    private boolean isInitialized;

    private static String TYPE = "INT";

    public AstVariableInt() {
        isInitialized = true;
    }

    public AstVariableInt(int value) {
        this.value = value;
        isInitialized = false;
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
    public String getValue() {
        return String.valueOf(value);
    }
}
