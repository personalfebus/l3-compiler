package ru.bmstu.iu9.personalfebus.compiler.generator;

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
}
