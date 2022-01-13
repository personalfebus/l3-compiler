package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

public interface AstVariable {
    String getType();
    boolean isInitialized();
    boolean isConstant();
    String getValue();
}
