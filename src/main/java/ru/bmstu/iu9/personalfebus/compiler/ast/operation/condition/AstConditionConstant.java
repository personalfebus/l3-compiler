package ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Set;

public class AstConditionConstant implements AstConditionPart {
    private String value;

    private static String TYPE = "CONDITION_CONSTANT";

    public AstConditionConstant(String value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        if (value.equalsIgnoreCase("tt")) {
            return "ldc.i4 1\n";
        } else if (value.equalsIgnoreCase("ff")) {
            return "ldc.i4 0\n";
        } else {
            throw new MissingException("boolean third value declaration :)");
        }
    }
}
