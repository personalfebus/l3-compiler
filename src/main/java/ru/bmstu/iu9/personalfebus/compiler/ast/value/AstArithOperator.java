package ru.bmstu.iu9.personalfebus.compiler.ast.value;

public class AstArithOperator implements AstArithExprPart {
    private char operator;

    private static String TYPE = "OPERATOR";

    public AstArithOperator(char operator) {
        this.operator = operator;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
