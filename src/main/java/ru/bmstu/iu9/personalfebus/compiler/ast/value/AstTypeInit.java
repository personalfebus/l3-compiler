package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstType;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Set;

public class AstTypeInit implements RValue {
    private final AstType type;
    private final RValue size;
    private final int depth;

    public AstTypeInit(AstType type, RValue size, int depth) {
        this.type = type;
        this.size = size;
        this.depth = depth;
    }

    public static String TYPE = "TYPE_INITIALIZATION";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        StringBuilder generatedCode = new StringBuilder();

        if (size.getType().equalsIgnoreCase(AstTypeInit.TYPE)) throw new TypeIncompatibilityException("constant", "array");

        generatedCode.append(size.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));
        generatedCode.append("newarr ")
                .append(type.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));

        for (int i = 1; i < depth; i++) {
            generatedCode.append("[]");
        }

        generatedCode.append('\n');
        return generatedCode.toString();
    }
}
