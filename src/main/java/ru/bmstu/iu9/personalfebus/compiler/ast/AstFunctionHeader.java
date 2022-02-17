package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;

import java.util.ArrayList;

public class AstFunctionHeader {
    private final String name;
    private ArrayList<AstIdentExpr> variables;
    private String returnType;

    public AstFunctionHeader(String name) {
        this.name = name;
        this.returnType = "void";
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void addVariable(AstIdentExpr variable) {
        variables.add(variable);
    }
}
