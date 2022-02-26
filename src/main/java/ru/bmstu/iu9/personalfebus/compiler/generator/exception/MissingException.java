package ru.bmstu.iu9.personalfebus.compiler.generator.exception;

import java.io.IOException;

public class MissingException extends IOException {
    private final String message;

    public MissingException(String message) {
        this.message = "Error: " + message + " is missing";
    }

    @Override
    public String toString() {
        return message;
    }
}
