package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Set;

public interface AstArithExprPart {
    String getType();
    String getConstantType();
    String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException;
}
