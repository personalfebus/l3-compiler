package ru.bmstu.iu9.personalfebus.compiler.generator;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;

import java.util.List;
import java.util.Set;

public interface Generatable {
    String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper) throws MissingException;
}
