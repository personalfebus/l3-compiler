package ru.bmstu.iu9.personalfebus.compiler.parser.exception;

import java.io.IOException;

public class BadSyntaxException extends IOException {
    private final String message;

    public BadSyntaxException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
