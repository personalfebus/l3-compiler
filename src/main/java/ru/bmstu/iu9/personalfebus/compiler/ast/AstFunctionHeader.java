package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstType;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;

import java.util.Set;

public class AstFunctionHeader {
    private final String name;
    private Set<AstVariable> variables;
    private AstType returnType;

    public AstFunctionHeader(String name) {
        this.name = name;
        this.returnType = null;
    }

    public void setReturnType(AstType returnType) {
        this.returnType = returnType;
    }

    public void setVariables(Set<AstVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(AstVariable variable) {
        variables.add(variable);
    }

    public String getName() {
        return name;
    }
}
