package ru.bmstu.iu9.personalfebus.compiler.ast.value;

public class AstBooleanConstant implements RValue {
    private boolean value;

    private static String TYPE = "BOOLEAN_VALUE";

    public AstBooleanConstant(boolean value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
