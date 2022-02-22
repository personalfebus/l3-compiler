package ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition;

public class AstConditionSeparator implements AstConditionPart {
    /**
     * 2 типа -
     * SEPARATOR_OPEN
     * SEPARATOR_CLOSE
     */
    private String type;

    public AstConditionSeparator(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}
