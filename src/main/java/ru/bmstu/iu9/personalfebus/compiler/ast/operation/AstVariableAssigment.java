package ru.bmstu.iu9.personalfebus.compiler.ast.operation;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;

import java.util.Set;

public class AstVariableAssigment implements AstOperation {
    private final Set<AstVariable> variables;

    public static String TYPE = "ASSIGMENT";

    public AstVariableAssigment(Set<AstVariable> variables) {
        this.variables = variables;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    //todo
    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, StringBuilder locals) throws MissingException {
        StringBuilder generatedCode = new StringBuilder();

        for (AstVariable variable : variables) {
            generatedCode.append(variable.generateAssigmentIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper));
        }

        return generatedCode.toString();
    }
}
