package ru.bmstu.iu9.personalfebus.compiler.ast;

import java.util.ArrayList;

public class AstFunction {
    private final AstFunctionHeader header;
    private final AstFunctionBody body;

    public AstFunction(AstFunctionHeader header, AstFunctionBody body) {
        this.header = header;
        this.body = body;
    }
}
