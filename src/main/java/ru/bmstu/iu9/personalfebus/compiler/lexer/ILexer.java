package ru.bmstu.iu9.personalfebus.compiler.lexer;

import ru.bmstu.iu9.personalfebus.compiler.lexer.token.Token;

public interface ILexer {
    boolean hasTokens();
    Token nextToken();
}
