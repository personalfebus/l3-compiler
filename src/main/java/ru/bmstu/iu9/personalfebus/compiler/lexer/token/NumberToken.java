package ru.bmstu.iu9.personalfebus.compiler.lexer.token;

import ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception.BadNumberSyntaxError;

public class NumberToken implements Token {
    private final int number;
    private final int line;
    private final int position;

    private static final String TYPE = "NUMBER";

    public NumberToken(String input, int line, int position) throws BadNumberSyntaxError {
        this.line = line;
        this.position = position;
        if (input.length() > 1 && input.charAt(0) == '0' && input.charAt(1) == 'x') {
            this.number = construct16(input);
        } else {
            this.number = construct10(input);
        }
    }

    private int construct16(String input) throws BadNumberSyntaxError {
        // 0x[0-9a-fA-F]+
        int a = 0;
        if (input.length() < 3) {
            // 0x - error or 0
            throw new BadNumberSyntaxError(line, position);
        }

        for (int i = 2; i < input.length(); i++) {
            int digit = lex16Symbol(input.charAt(i));
            a = a*16 + digit;
        }
        return a;
    }

    private int construct10(String input) throws BadNumberSyntaxError {
        //[0-9]+
        int a = 0;
        for (int i = 0; i < input.length(); i++) {
            int digit = lex10Symbol(input.charAt(i));
            a = a*10 + digit;
        }
        return a;
    }

    private int lex16Symbol(int code) throws BadNumberSyntaxError {
        if (code > 47 && code < 58) {
            return code - 48;
        } else if (code > 64 && code < 71) {
            return code - 55;
        } else if (code > 96 && code < 103) {
            return code - 87;
        } else throw new BadNumberSyntaxError(line, position);
    }

    private int lex10Symbol(int code) throws BadNumberSyntaxError {
        if (code > 47 && code < 58) {
            return code - 48;
        } else throw new BadNumberSyntaxError(line, position);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getBody() {
        return String.valueOf(number);
    }
}
