package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

public class AstType {
    private String typeName;
    private boolean isArray;
    private int arrayDepth;

    public AstType(String typeName, boolean isArray, int arrayDepth) {
        this.typeName = typeName;
        this.isArray = isArray;
        this.arrayDepth = arrayDepth;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isArray() {
        return isArray;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }
}
