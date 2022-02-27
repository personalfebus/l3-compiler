package ru.bmstu.iu9.personalfebus.compiler.ast.operation;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.AlreadyDeclaredException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Set;

public class AstVariableInitialization implements AstOperation {
    private final Set<AstVariable> variables;

    public static String TYPE = "INITIALIZATION";

    public AstVariableInitialization(Set<AstVariable> variables) {
        this.variables = variables;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, StringBuilder locals, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException, AlreadyDeclaredException {
        StringBuilder generatedCode = new StringBuilder();

        for (AstVariable var : variables) {
            if (formalParameters.hasVariable(var) || declaredVariables.hasVariable(var) || currentFunction.getHeader().getName().equalsIgnoreCase(var.getExpr().getName())) {
                throw new AlreadyDeclaredException(var.getExpr().getName());
            }

            declaredVariables.addVariable(var);
            int pos = declaredVariables.getVariableIndex(var);
            locals.append(var.getType().generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction))
                    .append(" ").append("var")
                    .append(pos)
                    .append(", ");

            if (var.getrValue() != null) {
                generatedCode.append(var.generateAssigmentIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));
            }
        }

        return generatedCode.toString();
    }
}
