package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Set;

public class AstArithExprSeparator implements AstArithExprPart {
    /**
     * 2 типа -
     * SEPARATOR_OPEN
     * SEPARATOR_CLOSE
     */
    private String type;

    public AstArithExprSeparator(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getConstantType() {
        return null;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        return "WTF - SEPARATOR IN CODE GEN!";
    }
}
