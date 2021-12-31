package ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception;

import java.io.IOException;

public class BadNumberSyntaxError extends IOException {
    private final int line;
    private final int position;

    public BadNumberSyntaxError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Bad Number Syntax at (").append(line).append(", ").append(position).append(");\n");
        return builder.toString();
    }
}
