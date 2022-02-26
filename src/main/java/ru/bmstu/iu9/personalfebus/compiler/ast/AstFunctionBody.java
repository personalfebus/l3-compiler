package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.generator.Generatable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;

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

    //todo
    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper) throws MissingException {
        StringBuilder generatedCode = new StringBuilder();
        for (AstOperation operation : operations) {

        }
        return "";
    }
}
