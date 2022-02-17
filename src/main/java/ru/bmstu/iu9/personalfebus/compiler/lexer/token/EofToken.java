package ru.bmstu.iu9.personalfebus.compiler.lexer.token;

public class EofToken implements Token {
    public static String TYPE = "EOF";

    public EofToken() {

    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getBody() {
        return null;
    }
}
