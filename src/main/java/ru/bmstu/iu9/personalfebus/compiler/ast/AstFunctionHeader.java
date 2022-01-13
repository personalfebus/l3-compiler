package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;

import java.util.ArrayList;

public class AstFunctionHeader {
    private final String name;
    private ArrayList<AstVariable> variables;
    private final String returnType;

    public AstFunctionHeader(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    public void addVariable(AstVariable variable) {
        variables.add(variable);
    }
}
