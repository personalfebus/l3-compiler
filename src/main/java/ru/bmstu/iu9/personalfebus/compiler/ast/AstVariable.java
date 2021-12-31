package ru.bmstu.iu9.personalfebus.compiler.ast;

public interface AstVariable {
    String getType();
    boolean isInitialized();
    String getValue();
}
