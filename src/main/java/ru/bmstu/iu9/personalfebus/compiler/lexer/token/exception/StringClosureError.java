package ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception;

import java.io.IOException;

public class StringClosureError extends IOException {
    private final int line;
    private final int position;

    public StringClosureError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("String Closure Error at (").append(line).append(", ").append(position).append(");\n");
        return builder.toString();
    }
}
