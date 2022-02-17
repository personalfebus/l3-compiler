package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;

import java.util.ArrayList;

public class AstFunctionHeader {
    private final String name;
    private ArrayList<AstIdentExpr> variables;
    private final String returnType;

    public AstFunctionHeader(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    public void addVariable(AstIdentExpr variable) {
        variables.add(variable);
    }
}
