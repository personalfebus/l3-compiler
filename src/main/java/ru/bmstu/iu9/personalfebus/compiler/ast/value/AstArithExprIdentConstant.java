package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import java.util.ArrayList;

public class AstArithExprIdentConstant implements AstArithExprPart, RValue {
    private final String name;
    private final ArrayList<String> tail;

    private static String TYPE = "ARITHMETIC_CONSTANT";

    public AstArithExprIdentConstant(String name, ArrayList<String> tail) {
        this.name = name;
        this.tail = tail;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getConstantType() {
        return "IDENT_CONSTANT";
    }
}
