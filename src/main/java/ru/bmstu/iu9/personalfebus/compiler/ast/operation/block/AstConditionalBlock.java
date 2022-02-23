package ru.bmstu.iu9.personalfebus.compiler.ast.operation.block;

import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition.AstCondition;

import java.util.ArrayList;
import java.util.List;

public class AstConditionalBlock implements AstOperation {
    private List<AstConditionalSubBlock> blocks;

    public final static String TYPE = "IF_BLOCK";

    public AstConditionalBlock() {
        blocks = new ArrayList<>();
    }

    public void addBlock(AstConditionalSubBlock block) {
        blocks.add(block);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
