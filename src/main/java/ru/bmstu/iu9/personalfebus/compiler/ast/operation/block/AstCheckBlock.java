package ru.bmstu.iu9.personalfebus.compiler.ast.operation.block;

import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition.AstCondition;

public class AstCheckBlock implements AstOperation {
    private final AstCondition condition;

    public final static String TYPE = "CHECK_BLOCK";

    public AstCheckBlock(AstCondition condition) {
        this.condition = condition;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
