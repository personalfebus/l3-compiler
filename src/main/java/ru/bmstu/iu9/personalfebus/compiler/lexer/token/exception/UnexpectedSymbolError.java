package ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception;

import java.io.IOException;

public class UnexpectedSymbolError extends IOException {
    private final int line;
    private final int position;

    public UnexpectedSymbolError(int line, int position) {
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Unexpected Symbol at (").append(line).append(", ").append(position).append(");\n");
        return builder.toString();
    }
}
