package ru.bmstu.iu9.personalfebus.compiler.parser.exception;

import java.io.IOException;

public class AlreadyDeclaredException extends IOException {
    private final String message;

    public AlreadyDeclaredException(String message) {
        this.message = "Error: " + message + " is already declared";
    }

    @Override
    public String toString() {
        return message;
    }
}
