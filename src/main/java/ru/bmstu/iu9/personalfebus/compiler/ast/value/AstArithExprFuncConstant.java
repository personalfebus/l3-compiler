package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import java.util.List;

public class AstArithExprFuncConstant implements AstArithExprPart, RValue {
    private final String name;
    private final List<RValue> arguments;

    private static String TYPE = "ARITHMETIC_CONSTANT";

    public AstArithExprFuncConstant(String name, List<RValue> arguments) {
        this.name = name;
        this.arguments = arguments;
    }



    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getConstantType() {
        return "FUNC_CONSTANT";
    }
}
