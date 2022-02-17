package ru.bmstu.iu9.personalfebus.compiler.lexer.token;

import ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception.BadNumberSyntaxError;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception.SymbolClosureError;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception.SymbolInterpretationException;

public class SymbolToken implements Token {
    private final int line;
    private final int position;
    private final char body;

    public static final String TYPE = "SYMBOL";

    public SymbolToken(int line, int position, String body) throws SymbolInterpretationException {
        this.line = line;
        this.position = position;
        this.body = interpretBody(body);
    }

    private char interpretBody(String str) throws SymbolInterpretationException {
        if (str.length() == 0) return 0;
        if (str.length() == 1) return str.charAt(0);
        assertChar(str, 0, '$');
        if (str.length() == 2) {
            assertChar(str, 1, '$');
            return 36;
        }

        switch (str.charAt(1)) {
            case '@' : {
                assertChar(str, 2, '$');
                if (str.length() > 3) throw new SymbolInterpretationException(line, position);
                return 7;
            }
            case '<' : {
                if (str.charAt(2) == '<') {
                    assertChar(str, 3, '$');
                    if (str.length() > 4) throw new SymbolInterpretationException(line, position);
                    return 13;
                } else if (str.charAt(2) == '$') {
                    if (str.length() > 3) throw new SymbolInterpretationException(line, position);
                    return 8;
                } else throw new SymbolInterpretationException(line, position);
            }
            case '>' : {
                assertChar(str, 2, '$');
                if (str.length() > 3) throw new SymbolInterpretationException(line, position);
                return 9;
            }
            case '^' : {
                assertChar(str, 2, '$');
                if (str.length() > 3) throw new SymbolInterpretationException(line, position);
                return 10;
            }
            case '\"' : {
                assertChar(str, 2, '$');
                if (str.length() > 3) throw new SymbolInterpretationException(line, position);
                return 34;
            }
            case '\'' : {
                assertChar(str, 2, '$');
                if (str.length() > 3) throw new SymbolInterpretationException(line, position);
                return 39;
            }
            default: {
                //x16
                try {
                    NumberToken tok = new NumberToken(str.substring(1, str.length() - 1), 0, 0);
                    return (char)tok.getNumber();
                } catch (BadNumberSyntaxError badNumberSyntaxError) {
                    //badNumberSyntaxError.printStackTrace();
                    throw new SymbolInterpretationException(line, position);
                }
            }
        }
    }

    private void assertChar(String str, int i, char a) throws SymbolInterpretationException {
        if (str.charAt(i) != a) throw new SymbolInterpretationException(line, position);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getBody() {
        return String.valueOf(body);
    }
}
