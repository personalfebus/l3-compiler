package ru.bmstu.iu9.personalfebus.compiler.parser;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstProgram;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition.BadConditionExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.*;

public interface IParser {
    //return ast tree here
    AstProgram parse() throws SyntaxException, BadArithmeticExpressionException, BadSyntaxException, NotAnArithmeticExpressionError, BadConditionExpressionException, AlreadyDeclaredException;
}
