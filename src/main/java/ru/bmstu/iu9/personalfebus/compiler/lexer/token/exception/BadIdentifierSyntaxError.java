package ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception;

import java.io.IOException;

public class BadIdentifierSyntaxError extends IOException {
    private final int line;
    private final int position;

    public BadIdentifierSyntaxError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Bad Identifier Syntax at (").append(line).append(", ").append(position).append(");\n");
        return builder.toString();
    }
}
