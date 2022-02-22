package ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;

public class AstConditionConstantRValue implements AstConditionPart {
    private RValue rValue;

    public AstConditionConstantRValue(RValue rValue) {
        this.rValue = rValue;
    }

    @Override
    public String getType() {
        return "CONDITION_RVALUE";
    }
}
