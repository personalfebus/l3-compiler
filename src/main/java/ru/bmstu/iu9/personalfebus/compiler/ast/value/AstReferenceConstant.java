package ru.bmstu.iu9.personalfebus.compiler.ast.value;

public class AstReferenceConstant implements RValue {
    private String typeName;
    private int arrayDepth;
    private int arraySize;

    
    public AstReferenceConstant(String typeName, int arrayDepth, int arraySize) {
        this.typeName = typeName;
        this.arrayDepth = arrayDepth;
        this.arraySize = arraySize;
    }

    @Override
    public String getType() {
        return null;
    }
}
