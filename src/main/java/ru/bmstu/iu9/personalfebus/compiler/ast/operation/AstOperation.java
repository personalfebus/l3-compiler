package ru.bmstu.iu9.personalfebus.compiler.ast.operation;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.AlreadyDeclaredException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Set;

public interface AstOperation {
    String getType();
    String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters,
                      VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper,
                      StringBuilder locals, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException, AlreadyDeclaredException, BadArithmeticExpressionException;
}
