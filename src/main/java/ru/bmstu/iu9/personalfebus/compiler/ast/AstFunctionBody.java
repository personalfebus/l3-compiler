package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.generator.Generatable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.AlreadyDeclaredException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AstFunctionBody implements Generatable {
    private List<AstOperation> operations;

    public AstFunctionBody() {
        this.operations = new ArrayList<>();
    }

    public void addOperation(AstOperation operation) {
        this.operations.add(operation);
    }

    //todo - ??
    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException, AlreadyDeclaredException, BadArithmeticExpressionException {
        StringBuilder generatedCode = new StringBuilder();
        StringBuilder locals = new StringBuilder(".locals (");

        for (AstOperation operation : operations) {
            generatedCode.append(operation.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        }

        locals.replace(locals.length() - 2, locals.length(), ")\n");
        return locals.toString() + generatedCode.toString();
    }
}
