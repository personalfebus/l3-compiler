package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.generator.Generatable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;

import java.util.Set;

public class AstType implements Generatable {
    private final String typeName;
    private final boolean isArray;
    private final int arrayDepth;

    public AstType(String typeName, boolean isArray, int arrayDepth) {
        this.typeName = typeName;
        this.isArray = isArray;
        this.arrayDepth = arrayDepth;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isArray() {
        return isArray;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) {
        String name;
        if (typeName.equalsIgnoreCase("int")) {
            name = "int32";
        } else name = typeName;

        StringBuilder generatedCode = new StringBuilder(name);
        String closure = "[]";

        for (int i = 0; i < arrayDepth; i++) {
            generatedCode.append(closure);
        }

        return generatedCode.toString();
    }
}
