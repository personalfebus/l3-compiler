package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

public class AstVariableChar implements AstVariable {
    private final String name;
    private final boolean isConstant;
    private boolean isInitialized;

    private char value;

    private static String TYPE = "CHAR";

    public AstVariableChar(char value) {
        this.name = "";
        isConstant = true;
        this.value = value;
        isInitialized = true;
    }

    public AstVariableChar(String name) {
        this.name = name;
        isInitialized = false;
        isConstant = false;
    }

    public AstVariableChar(String name, char value) {
        this.name = name;
        this.value = value;
        isConstant = false;
        isInitialized = true;
    }

    public void setValue(char value) {
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
