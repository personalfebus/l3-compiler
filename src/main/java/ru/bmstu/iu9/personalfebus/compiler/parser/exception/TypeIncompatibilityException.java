package ru.bmstu.iu9.personalfebus.compiler.parser.exception;

import java.io.IOException;

public class TypeIncompatibilityException extends IOException {
    private final String message;

    public TypeIncompatibilityException(String type1, String type2) {
        this.message = "Error: trying to aggregate incompatible types: " + type1 + ", " + type2;
    }

    @Override
    public String toString() {
        return message;
    }
}
