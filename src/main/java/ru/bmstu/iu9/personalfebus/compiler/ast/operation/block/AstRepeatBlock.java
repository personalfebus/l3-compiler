package ru.bmstu.iu9.personalfebus.compiler.ast.operation.block;

import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition.AstCondition;

import java.util.List;

public class AstRepeatBlock implements AstOperation {
    private final AstCondition condition;
    private final List<AstOperation> operations;

    public final static String TYPE = "REPEAT_BLOCK";

    public AstRepeatBlock(AstCondition condition, List<AstOperation> operations) {
        this.condition = condition;
        this.operations = operations;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
