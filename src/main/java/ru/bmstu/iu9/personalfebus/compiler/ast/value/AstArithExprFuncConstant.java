package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstFunctionCallOperation;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.List;
import java.util.Set;

public class AstArithExprFuncConstant implements AstArithExprPart, RValue {
    private final String name;
    private final List<RValue> arguments;

    private static String TYPE = "ARITHMETIC_CONSTANT";

    public AstArithExprFuncConstant(String name, List<RValue> arguments) {
        this.name = name;
        this.arguments = arguments;
    }



    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getConstantType() {
        return "FUNC_CONSTANT";
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        AstFunctionCallOperation op = new AstFunctionCallOperation(name);
        op.setArguments(arguments);

        return op.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction);
    }
}
