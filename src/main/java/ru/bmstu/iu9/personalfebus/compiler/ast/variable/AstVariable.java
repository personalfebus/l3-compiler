package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;
import ru.bmstu.iu9.personalfebus.compiler.generator.Generatable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AstVariable implements Generatable {
    private final AstIdentExpr expr;
    private AstType type;
    private RValue rValue;

    public AstVariable(AstIdentExpr expr) {
        this.expr = expr;
    }

    public AstVariable(AstIdentExpr expr, RValue rValue) {
        this.expr = expr;
        this.rValue = rValue;
    }

    public boolean isInitialized() {
        return rValue != null;
    }

    public AstIdentExpr getExpr() {
        return expr;
    }

    public AstType getType() {
        return type;
    }

    public RValue getrValue() {
        return rValue;
    }

    public void setType(AstType type) {
        this.type = type;
    }

    public void setrValue(RValue rValue) {
        this.rValue = rValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AstVariable variable = (AstVariable) o;
        return expr.equals(variable.expr) && type.equals(variable.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr, type);
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper) throws MissingException {
        return null;
    }

    public String generateAssigmentIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper) throws MissingException {
        StringBuilder generatedCode = new StringBuilder();
        String storeCommand = "";
        String loadCommand = "";

        if (formalParameters.hasVariable(this)) {
            int pos = formalParameters.getVariableIndex(this);
            storeCommand = "ldarg." + pos;
            loadCommand = "starg." + pos;
        } else if (declaredVariables.hasVariable(this)) {
            int pos = declaredVariables.getVariableIndex(this);
            storeCommand = "ldloc." + pos;
            loadCommand = "stloc." + pos;
        } else {
            throw new MissingException("Declaration of variable " + this.expr.getName());
        }

        //todo everything else || also can have array assigment here :)

        return generatedCode.toString();
    }

    public String generateDeclarationIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper) {
        StringBuilder generatedCode = new StringBuilder();

        generatedCode.append(type.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper));
        generatedCode.append(" par")
                .append(formalParameters.getVariableIndex(this));

        return generatedCode.toString();
    }
}
