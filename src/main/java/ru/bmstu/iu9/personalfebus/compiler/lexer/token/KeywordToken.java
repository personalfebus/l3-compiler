package ru.bmstu.iu9.personalfebus.compiler.lexer.token;

public class KeywordToken implements Token {
    private final int line;
    private final int position;
    private final String body;

    private static final String TYPE = "KEYWORD";

    public KeywordToken(int line, int position, String body) {
        this.line = line;
        this.position = position;
        this.body = body;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getBody() {
        return body;
    }
}
