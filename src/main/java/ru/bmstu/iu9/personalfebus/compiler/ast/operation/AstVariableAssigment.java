package ru.bmstu.iu9.personalfebus.compiler.ast.operation;

import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;

import java.util.Set;

public class AstVariableAssigment implements AstOperation {
    private final Set<AstVariable> variables;

    public static String TYPE = "ASSIGMENT";

    public AstVariableAssigment(Set<AstVariable> variables) {
        this.variables = variables;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
