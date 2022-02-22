package ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition;

public class AstConditionConstant implements AstConditionPart {
    private String value;

    private static String TYPE = "CONDITION_CONSTANT";

    public AstConditionConstant(String value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
