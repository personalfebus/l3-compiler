package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Set;

public class AstArithExprConstant implements AstArithExprPart, RValue {
    private String value;
    private String subType = "number";

    private static String TYPE = "ARITHMETIC_CONSTANT";


    public AstArithExprConstant(String value) {
        this.value = value;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getConstantType() {
        return "CONSTANT";
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        if (subType.equalsIgnoreCase("number")) {
            return "ldc.i4 " + Integer.parseInt(value) + "\n";
        } else if (subType.equalsIgnoreCase("char")) {
            return "ldc.i4 " + (int)value.charAt(0) + "\n";
        } else if (value.equals("tt")) {
            return "ldc.i4 1\n";
        } else {
            return "ldc.i4 0\n";
        }
    }
}
