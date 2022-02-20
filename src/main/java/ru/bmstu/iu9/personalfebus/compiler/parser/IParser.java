package ru.bmstu.iu9.personalfebus.compiler.parser;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstProgram;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadSyntaxException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.NotAnArithmeticExpressionError;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.SyntaxException;

public interface IParser {
    //return ast tree here
    AstProgram parse() throws SyntaxException, BadArithmeticExpressionException, BadSyntaxException, NotAnArithmeticExpressionError;
}
