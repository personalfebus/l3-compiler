package ru.bmstu.iu9.personalfebus.compiler.ast.value;

public class AstArithExprConstant implements AstArithExprPart, RValue {
    private String value;

    private static String TYPE = "ARITHMETIC_CONSTANT";

    public AstArithExprConstant(String value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getConstantType() {
        return "CONSTANT";
    }
}
