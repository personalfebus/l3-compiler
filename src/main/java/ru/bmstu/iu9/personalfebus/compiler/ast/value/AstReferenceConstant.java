package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Set;

public class AstReferenceConstant implements RValue {
    private String typeName;
    private int arrayDepth;
    private int arraySize;

    
    public AstReferenceConstant(String typeName, int arrayDepth, int arraySize) {
        this.typeName = typeName;
        this.arrayDepth = arrayDepth;
        this.arraySize = arraySize;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        return "unused class";
    }
}
