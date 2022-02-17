package ru.bmstu.iu9.personalfebus.compiler.parser;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunctionHeader;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstProgram;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.lexer.ILexer;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.IdentifierToken;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.KeywordToken;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.OperatorToken;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.Token;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadSyntaxException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.SyntaxException;

import java.util.ArrayList;

public class Parser implements IParser {
    private final ILexer lexer;

    public Parser(ILexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public AstProgram parse() {
        return parseProgram();
    }

    private Token assertNextToken(String type) throws SyntaxException {
        if (lexer.hasTokens()) {
            Token nextToken = lexer.nextToken();
            if (!nextToken.getType().equals(type)) {
                throw new SyntaxException(type, nextToken.getType());
            }

            return nextToken;
        } else {
            throw new SyntaxException(type, null);
        }
    }

    private Token assertBody(Token token, String expected) throws SyntaxException {
        if (token.getBody().equalsIgnoreCase(expected)) {
            return token;
        } else throw new SyntaxException(expected, token.getBody());
    }

    private AstProgram parseProgram() {
        AstProgram program = new AstProgram();

        for (;;) {
            AstFunction function = parseFunction();
            program.addFunction(function);

            if (!lexer.hasTokens()) { //посмотреть eof
                break;
            }
        }

        return program;
    }

    private AstFunction parseFunction() throws SyntaxException, BadSyntaxException {
        if (!lexer.hasTokens()) {
            throw new SyntaxException(KeywordToken.TYPE, null);
        }

        Token token = assertNextToken(KeywordToken.TYPE);

        if (token.getBody().equalsIgnoreCase("func")) {
            //function
            AstFunctionHeader header = parseFunctionHeader();
        } else if (token.getBody().equalsIgnoreCase("proc")) {
            //procedure
            AstFunctionHeader header = parseProcedureHeader();
        } else throw new BadSyntaxException("Bad syntax in function header: expected <func> or <proc> or <main>");

        return null; //TODO
    }

    private AstFunctionHeader parseFunctionHeader() throws SyntaxException {
        Token name = assertNextToken(IdentifierToken.TYPE);
        assertBody(assertNextToken(OperatorToken.TYPE), "(");

    }

    private AstFunctionHeader parseProcedureHeader() {

    }

    private ArrayList<AstIdentExpr> parseHeaderVariables() {

    }
}
