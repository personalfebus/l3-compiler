package ru.bmstu.iu9.personalfebus.compiler.generator;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;

import java.util.ArrayList;
import java.util.List;

public class VariableNameTranslator {
    private List<AstVariable> variables;

    public VariableNameTranslator() {
        variables = new ArrayList<>();
    }

    public void setVariables(List<AstVariable> variables) {
        this.variables = variables;
    }

    public boolean hasVariable(AstVariable variable) {
        return variables.contains(variable);
    }

    public int getVariableIndex(AstVariable variable) {
        return variables.indexOf(variable);
    }

    public boolean hasByIdentExpr(AstIdentExpr expr) {
        for (AstVariable variable : variables) {
            if (variable.getExpr().equals(expr)) {
                return true;
            }
        }

        return false;
    }

    public int findByIdentExpr(AstIdentExpr expr) {
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).getExpr().equals(expr)) {
                return i;
            }
        }

        return -1;
    }

    public void addVariable(AstVariable variable) {
        variables.add(variable);
    }
}
