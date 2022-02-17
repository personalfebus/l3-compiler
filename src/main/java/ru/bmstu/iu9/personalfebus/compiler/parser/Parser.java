package ru.bmstu.iu9.personalfebus.compiler.parser;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunctionHeader;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstProgram;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.lexer.ILexer;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.*;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadSyntaxException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.SyntaxException;

import java.util.ArrayList;

public class Parser implements IParser {
    private final ILexer lexer;
    private Token currentToken;

    public Parser(ILexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public AstProgram parse() {
        return parseProgram();
    }

    private void nextToken() {
        if (lexer.hasTokens()) currentToken = lexer.nextToken();
        else currentToken = new EofToken();
    }

    private void assertTokenType(String type) throws SyntaxException {
        if (!currentToken.getType().equalsIgnoreCase(type)) {
            throw new SyntaxException(type, currentToken.getType());
        }
    }

    private void assertTokenBody(String body) throws SyntaxException {
        if (!currentToken.getBody().equalsIgnoreCase(body)) {
            throw new SyntaxException(body, currentToken.getBody());
        }
    }

    private AstProgram parseProgram() throws SyntaxException, BadSyntaxException {
        AstProgram program = new AstProgram();

        for (;;) {
            AstFunction function = parseFunction();
            program.addFunction(function);

            if (currentToken.getType().equalsIgnoreCase(EofToken.TYPE)) { //посмотреть eof
                break;
            }
        }

        return program;
    }

    private AstFunction parseFunction() throws SyntaxException, BadSyntaxException {
        nextToken();
        assertTokenType(KeywordToken.TYPE);

        if (currentToken.getBody().equalsIgnoreCase("func")) {
            //function
            AstFunctionHeader header = parseFunctionHeader();
        } else if (currentToken.getBody().equalsIgnoreCase("proc")) {
            //procedure
            AstFunctionHeader header = parseProcedureHeader();
        } else throw new BadSyntaxException("Bad syntax in function header: expected <func> or <proc> or <main>");

        return null; //TODO
    }

    private AstFunctionHeader parseFunctionHeader() throws SyntaxException {
        nextToken();
        assertTokenType(IdentifierToken.TYPE);
        Token name = currentToken;

        nextToken();
        assertTokenType(OperatorToken.TYPE);
        assertTokenBody("(");

        AstFunctionHeader header = new AstFunctionHeader(name.getBody());

        while (currentToken.getType().equalsIgnoreCase(EofToken.TYPE)) {
            nextToken();

            if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase(")")) {
                break;
            }

            header.addVariable(parseIdentExpr());
        }
    }

    private AstFunctionHeader parseProcedureHeader() {

    }

    private

    private AstIdentExpr parseIdentExpr() throws SyntaxException {
        assertTokenType(IdentifierToken.TYPE);
        AstIdentExpr expr = new AstIdentExpr(currentToken.getBody());
        nextToken();

        while (currentToken.getType().equalsIgnoreCase(EofToken.TYPE)) {
            if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase("[")) {
                nextToken();
                assertTokenType(NumberToken.TYPE);
                expr.addTail(Integer.parseInt(currentToken.getBody()));

                nextToken();
                assertTokenType(NumberToken.TYPE);
                assertTokenBody("]");

                nextToken();
            } else break;
        }

        return expr;
    }
}
