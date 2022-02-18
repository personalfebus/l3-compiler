package ru.bmstu.iu9.personalfebus.compiler.ast.operation;

import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;

import java.util.Set;

public class AstVariableInitialization implements AstOperation {
    private final Set<AstVariable> variables;
    public static String TYPE = "INITIALIZATION";

    public AstVariableInitialization(Set<AstVariable> variables) {
        this.variables = variables;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
