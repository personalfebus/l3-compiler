package ru.bmstu.iu9.personalfebus.compiler.parser.exception;

import ru.bmstu.iu9.personalfebus.compiler.lexer.token.Token;

import java.io.IOException;

public class SyntaxException extends IOException {
    private final String message;

    public SyntaxException(String expected, String found) {
        if (expected == null) {
            message = "Syntax Error: Expected eof, found " + found + "\n";
        } else if (found == null) {
            message = "Syntax Error: Expected " + expected + ", found eof\n";
        } else {
            message = "Syntax Error: Expected " + expected + ", found " + found + "\n";;
        }
    }

    @Override
    public String toString() {
        return message;
    }
}
