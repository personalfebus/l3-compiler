package ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Set;

public class AstConditionConstantRValue implements AstConditionPart {
    private RValue rValue;

    public AstConditionConstantRValue(RValue rValue) {
        this.rValue = rValue;
    }

    @Override
    public String getType() {
        return "CONDITION_RVALUE";
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        return rValue.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction);
    }
}
