package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstType;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class AstIdentExpr implements RValue {
    private String name;
    private ArrayList<String> tail;

    private static String TYPE = "IDENTIFIER";

    public AstIdentExpr(String name) {
        this.name = name;
        this.tail = new ArrayList<>();
    }

    public void setTail(ArrayList<String> tail) {
        this.tail = tail;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getTail() {
        return tail;
    }

    public void addTail(String a) {
        tail.add(a);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException {
        String storeCommand = "";
        String loadCommand = "";

        if (formalParameters.hasByIdentExpr(this)) {
            int pos = formalParameters.findByIdentExpr(this);
            storeCommand = "starg." + pos;
            loadCommand = "ldarg." + pos;
        } else if (declaredVariables.hasByIdentExpr(this)) {
            int pos = declaredVariables.findByIdentExpr(this);
            storeCommand = "stloc." + pos;
            loadCommand = "ldloc." + pos;
        } else {
            throw new MissingException("Declaration of variable " + this.getName());
        }

        return generateAssigmentLValueIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, storeCommand, loadCommand, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AstIdentExpr expr = (AstIdentExpr) o;
        return name.equals(expr.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String generateAssigmentLValueIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, String storeCommand, String loadCommand, boolean needToStore) throws MissingException {
        StringBuilder generatedCode = new StringBuilder();

        if (tail.size() == 0) {
            if (needToStore) {
                generatedCode.append(storeCommand)
                        .append('\n');
            } else {
                generatedCode.append(loadCommand)
                        .append('\n');
            }
        }

        for (int i = 0; i < tail.size(); i++) {
            if (i == 0) {
                generatedCode.append(loadCommand)
                        .append('\n');
            }

            if (Character.isDigit(tail.get(i).charAt(0))) {
                generatedCode.append("ldc.i4 ")
                        .append(Integer.parseInt(tail.get(i)))
                        .append('\n');
            } else {
                AstVariable parameter = new AstVariable(new AstIdentExpr(tail.get(i)));
                parameter.setType(new AstType("int", false, 0));

                String parStoreCommand = "";
                String parLoadCommand = "";

                if (formalParameters.hasVariable(parameter)) {
                    int pos = formalParameters.getVariableIndex(parameter);
                    parStoreCommand = "starg." + pos;
                    parLoadCommand = "ldarg." + pos;
                } else if (declaredVariables.hasVariable(parameter)) {
                    int pos = declaredVariables.getVariableIndex(parameter);
                    parStoreCommand = "stloc." + pos;
                    parLoadCommand = "ldloc." + pos;
                } else {
                    throw new MissingException("Declaration of variable " + tail.get(i));
                }

                generatedCode.append(parLoadCommand)
                        .append('\n');
            }



            if (tail.size() - i == 1) {
                if (needToStore) {
                    //generatedCode.append("stelem.i4");
                } else {
                    generatedCode.append("ldelem.i4");
                }
            } else {
                generatedCode.append("ldelem.ref");
            }

            generatedCode.append('\n');
        }

        return generatedCode.toString();
    }
}
