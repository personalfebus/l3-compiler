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

import java.util.Set;

public class AstCheckBlock implements AstOperation {
    private final AstCondition condition;

    public final static String TYPE = "CHECK_BLOCK";

    public AstCheckBlock(AstCondition condition) {
        this.condition = condition;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, StringBuilder locals, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException, AlreadyDeclaredException, BadArithmeticExpressionException {
        StringBuilder generatedCode = new StringBuilder();
        int num = labelGenerationHelper.getNum();
        generatedCode.append(condition.generatedIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        generatedCode.append("brtrue check_")
                .append(num)
                .append("\n");

        generatedCode.append("ldc.i4 1\n");
        generatedCode.append("throw\n");
        generatedCode.append("check_")
                .append(num)
                .append(":\n");

        return generatedCode.toString();
    }
}
