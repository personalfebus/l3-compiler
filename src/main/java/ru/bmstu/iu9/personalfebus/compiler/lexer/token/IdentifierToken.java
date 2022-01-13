package ru.bmstu.iu9.personalfebus.compiler.lexer.token;

import ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception.BadIdentifierSyntaxError;

public class IdentifierToken implements Token {
    private final int line;
    private final int position;
    private final String body;

    private static final String TYPE = "IDENTIFIER";

    public IdentifierToken(int line, int position, String body) throws BadIdentifierSyntaxError {
        this.line = line;
        this.position = position;
        this.body = body;
        assertBody();
    }

    private void assertBody() throws BadIdentifierSyntaxError {
        // [A-Za-z\_\?\$\@\.\#\~] [A-Za-z\_\?\$\@\.\#\~]*
        for (int i = 0; i < body.length(); i++) {
            if (!isValidSymbol(body.charAt(i))) {
                throw new BadIdentifierSyntaxError(line, position);
            }
        }
    }

    private boolean isValidSymbol(char a) {
        return (a >= 'a') && (a <= 'z') ||
                (a >= 'A') && (a <= 'Z') ||
                (a >= '0') && (a <= '9') ||
                (a == '_') || (a == '?') ||
                (a == '$') || (a == '@') ||
                (a == '.') || (a == '#') ||
                (a == '~');
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
