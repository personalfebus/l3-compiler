package ru.bmstu.iu9.personalfebus.compiler.ast.operation.block;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition.AstCondition;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.AlreadyDeclaredException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.List;
import java.util.Set;

public class AstConditionalSubBlock {
    private final AstCondition condition;
    private final List<AstOperation> operations;

    public AstConditionalSubBlock(AstCondition condition, List<AstOperation> operations) {
        this.condition = condition;
        this.operations = operations;
    }

    public String generatedIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, StringBuilder locals, AstFunction currentFunction) throws TypeIncompatibilityException, MissingException, BadArithmeticExpressionException, AlreadyDeclaredException {
        StringBuilder generatedCode = new StringBuilder();
        int num = labelGenerationHelper.getNum();

        if (condition != null) {
            generatedCode.append(condition.generatedIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
            generatedCode.append("brfalse if_skip_")
                    .append(num)
                    .append("\n");
        }

        for (AstOperation operation : operations) {
            generatedCode.append(operation.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        }
        generatedCode.append("if_skip_")
                .append(num)
                .append(":\n");

        return generatedCode.toString();
    }
}
