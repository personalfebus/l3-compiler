package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

import java.util.ArrayList;

public class AstVariableReference implements AstVariable{
    private final String name;
    private final boolean isConstant;
    private boolean isInitialized;

    private ArrayList<AstVariable> variables;
    private int size;

    private static String TYPE = "REF";

    public AstVariableReference() {
        this.isConstant = true;
        this.isInitialized = true;
        this.name = "";
    }

    public AstVariableReference(String name) {
        this.name = name;
        this.isConstant = false;
        this.isInitialized = false;
    }

    public AstVariableReference(String name, int size) {
        this.name = name;
        this.size = size;
        this.isConstant = false;
        this.isInitialized = true;
    }

    public void addVariable(AstVariable variable) {
        if (variables.size() < size) {
            variables.add(variable);
        } else {
            //todo error if len > size
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public String getValue() {
        return null;
    }

    public AstVariable get(int i) {
        if (i < size) {
            return variables.get(i);
        } else {
            //todo error if len > size
            return null;
        }
    }
}
