package ru.bmstu.iu9.personalfebus.compiler.ast.operation.block;

import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;

//todo
public class AstWhileBlock implements AstOperation {

    public final static String TYPE = "WHILE_BLOCK";

    @Override
    public String getType() {
        return TYPE;
    }
}
