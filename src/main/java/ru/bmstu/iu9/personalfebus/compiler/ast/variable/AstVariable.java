package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstArithExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstTypeInit;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;
import ru.bmstu.iu9.personalfebus.compiler.generator.Generatable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.Objects;
import java.util.Set;

public class AstVariable implements Generatable {
    private final AstIdentExpr expr;
    private AstType type;
    private RValue rValue = null;

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
        return expr.equals(variable.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr);
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException {
        return null;
    }

    public String generateAssigmentIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException {
        StringBuilder generatedCode = new StringBuilder();
        String storeCommand = "";
        String loadCommand = "";
        AstVariable actualVariable = null;
        if (formalParameters.hasVariable(this)) {
            int pos = formalParameters.getVariableIndex(this);
            storeCommand = "starg." + pos;
            loadCommand = "ldarg." + pos;
            actualVariable = formalParameters.getVariable(pos);
        } else if (declaredVariables.hasVariable(this)) {
            int pos = declaredVariables.getVariableIndex(this);
            storeCommand = "stloc." + pos;
            loadCommand = "ldloc." + pos;
            actualVariable = declaredVariables.getVariable(pos);
        } else if (currentFunction.getHeader().getName().equalsIgnoreCase(expr.getName())) {
            storeCommand = "ret";
            loadCommand = "dont_load_me_pls";
            type = currentFunction.getHeader().getReturnType();
        } else {
            throw new MissingException("Declaration of variable " + this.expr.getName());
        }

        if (type != null && expr.getTail().size() > type.getArrayDepth()) {
            // int[][] a;
            // a[][][] = 1;
            throw new TypeIncompatibilityException("constant", "void");
        } else if (type != null && expr.getTail().size() < type.getArrayDepth()) {
            if (rValue.getType().equalsIgnoreCase(AstTypeInit.TYPE)) {
                //check types
                //int[][] a;
                // a[] = [int 1];
                if (type.getTypeName().equalsIgnoreCase("bool")) {
                    AstArithExpr e = (AstArithExpr) rValue;
                    if (e.getParts().size() == 1 && e.getParts().get(0).getType().equalsIgnoreCase("BOOLEAN_VALUE")) {

                    } else {
                        throw new TypeIncompatibilityException("bool", "int");
                    }
                }
                System.out.println("----------");
                System.out.println(generatedCode);
                generatedCode.append(rValue.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));
                System.out.println("----------");
                System.out.println(generatedCode);
                generatedCode.append(expr.generateAssigmentLValueIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, storeCommand, loadCommand, true));
                System.out.println("----------");
                System.out.println(generatedCode);
            } else {
                //int[] a;
                // a = 1;
                throw new TypeIncompatibilityException("array", "constant");
            }
        } else {
            //normal
            if (expr.getTail().size() > 0) {
                System.out.println("----------");
                System.out.println(generatedCode);
                generatedCode.append(expr.generateAssigmentLValueIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, storeCommand, loadCommand, true));
                System.out.println("----------");
                System.out.println(generatedCode);
                generatedCode.append(rValue.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));

                if (actualVariable.type != null && actualVariable.type.getTypeName().equalsIgnoreCase("char")) {
                    generatedCode.append("stelem char\n");
                } else if (actualVariable.type != null && actualVariable.type.getTypeName().equalsIgnoreCase("bool")) {
                    generatedCode.append("stelem bool\n");
                } else {
                    generatedCode.append("stelem.i4\n");
                }
                System.out.println("----------");
                System.out.println(generatedCode);
            } else {
                generatedCode.append(rValue.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));
                generatedCode.append(expr.generateAssigmentLValueIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, storeCommand, loadCommand, true));
            }
        }

        return generatedCode.toString();
    }

    public String generateDeclarationIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) {
        StringBuilder generatedCode = new StringBuilder();

        generatedCode.append(type.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));
        generatedCode.append(" par")
                .append(formalParameters.getVariableIndex(this));

        return generatedCode.toString();
    }
}
