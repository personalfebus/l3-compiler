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

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public int getPosition() {
        return 0;
    }
}
