package ru.bmstu.iu9.personalfebus.compiler.ast.operation.model;

import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;

import java.util.Set;

public class InitializationOrAssigment {
    public Set<AstVariable> variables;
    public boolean isInit;

    public InitializationOrAssigment(Set<AstVariable> variables, boolean isInit) {
        this.variables = variables;
        this.isInit = isInit;
    }
}
