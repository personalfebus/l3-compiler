package ru.bmstu.iu9.personalfebus.compiler.ast.operation;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunctionHeader;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AstFunctionCallOperation implements RValue, AstOperation {
    private final String name;
    private List<RValue> arguments;

    private static String TYPE = "FUNCTION_CALL";

    public AstFunctionCallOperation(String name) {
        this.name = name;
        arguments = new ArrayList<>();
    }

    public void setArguments(List<RValue> arguments) {
        this.arguments = arguments;
    }

    public void addArgument(RValue value) {
        arguments.add(value);
    }

    public String getName() {
        return name;
    }

    public List<RValue> getArguments() {
        return arguments;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        StringBuilder generatedCode = new StringBuilder();
        StringBuilder funcCall = new StringBuilder();
        funcCall.append("call ");
        AstFunctionHeader targetFunction = null;

        for (AstFunction function : declaredFunctions) {
            if (function.getHeader().getName().equalsIgnoreCase(name)) {
                targetFunction = function.getHeader();
                break;
            }
        }
        
        if (targetFunction == null) {
            throw new MissingException("Declaration for function " + name + " ");
        }

        funcCall.append(targetFunction.getReturnType().generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction))
                .append(" ")
                .append(targetFunction.getName())
                .append("(");

        for (AstVariable v : targetFunction.getVariables()) {
            funcCall.append(v.getType().generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction))
                    .append(", ");
        }
        if (targetFunction.getVariables().size() > 0) {
            funcCall.replace(funcCall.length() - 2, funcCall.length(), ")\n");
        } else {
            funcCall.append(")\n");
        }

        if (arguments.size() != targetFunction.getVariables().size()) {
            throw new MissingException("Function declaration of " + targetFunction.getName() + " with " + arguments.size());
        }

        for (RValue rValue : arguments) {
            generatedCode.append(rValue.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));
        }

        generatedCode.append(funcCall);

        return generatedCode.toString();
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, StringBuilder locals, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        return generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction);
    }
}
