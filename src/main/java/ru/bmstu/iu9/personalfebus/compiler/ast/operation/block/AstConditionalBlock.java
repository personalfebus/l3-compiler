package ru.bmstu.iu9.personalfebus.compiler.ast.operation.block;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.AlreadyDeclaredException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AstConditionalBlock implements AstOperation {
    private List<AstConditionalSubBlock> blocks;

    public final static String TYPE = "IF_BLOCK";

    public AstConditionalBlock() {
        blocks = new ArrayList<>();
    }

    public void addBlock(AstConditionalSubBlock block) {
        blocks.add(block);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, StringBuilder locals, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException, AlreadyDeclaredException, BadArithmeticExpressionException {
        StringBuilder generatedCode = new StringBuilder();
        if (blocks.size() < 1) throw new MissingException("If body");

        for (AstConditionalSubBlock subBlock : blocks) {
            generatedCode.append(subBlock.generatedIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        }

        return generatedCode.toString();
    }
}
