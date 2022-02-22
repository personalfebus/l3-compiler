package ru.bmstu.iu9.personalfebus.compiler.ast.operation.block;

import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.model.InitializationOrAssigment;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;

import java.util.List;

public class AstForBlock implements AstOperation {
    private final InitializationOrAssigment ioa;
    private final RValue to;
    private final RValue step;
    private final List<AstOperation> operations;

    public final static String TYPE = "FOR_BLOCK";

    public AstForBlock(InitializationOrAssigment ioa, RValue to, RValue step, List<AstOperation> operations) {
        this.ioa = ioa;
        this.to = to;
        this.step = step;
        this.operations = operations;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
