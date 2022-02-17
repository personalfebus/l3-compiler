package ru.bmstu.iu9.personalfebus.compiler.lexer.token;

public class OperatorToken implements Token {
    private final int line;
    private final int position;
    private final String body;

    public static final String TYPE = "OPERATOR";

    public OperatorToken(int line, int position, String body) {
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
