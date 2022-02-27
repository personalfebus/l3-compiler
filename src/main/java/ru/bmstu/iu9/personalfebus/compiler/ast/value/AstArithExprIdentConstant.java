package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.ArrayList;
import java.util.Set;

public class AstArithExprIdentConstant implements AstArithExprPart, RValue {
    private final String name;
    private final ArrayList<String> tail;

    private static String TYPE = "ARITHMETIC_CONSTANT";

    public AstArithExprIdentConstant(String name, ArrayList<String> tail) {
        this.name = name;
        this.tail = tail;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getConstantType() {
        return "IDENT_CONSTANT";
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        AstIdentExpr ex = new AstIdentExpr(name);
        ex.setTail(tail);

        return ex.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction);
    }
}
