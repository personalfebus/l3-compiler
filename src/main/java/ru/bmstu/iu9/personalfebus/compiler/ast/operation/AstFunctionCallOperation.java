package ru.bmstu.iu9.personalfebus.compiler.ast.operation;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstArithExprPart;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;

//todo
public class AstFunctionCallOperation implements RValue, AstArithExprPart, AstOperation {

    private static String TYPE = "FUNCTION_CALL";

    @Override
    public String getType() {
        return TYPE;
    }
}
