package ru.bmstu.iu9.personalfebus.compiler.parser;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunctionBody;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunctionHeader;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstProgram;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstFunctionCallOperation;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstVariableInitialization;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstType;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.lexer.ILexer;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.*;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadSyntaxException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.SyntaxException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser implements IParser {
    private final ILexer lexer;
    private Token currentToken;
    private Token backlog;

    public Parser(ILexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public AstProgram parse() {
        return parseProgram();
    }

    private void nextToken() {
        if (backlog != null) {
            currentToken = backlog;
            backlog = null;
        }
        else if (lexer.hasTokens()) currentToken = lexer.nextToken();
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
            AstFunctionHeader header = parseFunctionHeader(false);
            AstFunctionBody body = parseFunctionBody(false);
            return new AstFunction(header, body);
        } else if (currentToken.getBody().equalsIgnoreCase("proc")) {
            //procedure
            AstFunctionHeader header = parseFunctionHeader(true);
            AstFunctionBody body = parseFunctionBody(true);
            return new AstFunction(header, body);
        } else {
            throw new BadSyntaxException("Bad syntax in function header: expected func or proc or main");
        }
    }

    private AstFunctionBody parseFunctionBody(boolean isProcedure) throws SyntaxException {
        AstFunctionBody body = new AstFunctionBody();
        for (;;) {
            body.addOperation(parseOperation());
            //assuming parseOperation has nextToken in the end

            if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)
                    && ((isProcedure && currentToken.getBody().equalsIgnoreCase("endproc"))
                        || (!isProcedure && currentToken.getBody().equalsIgnoreCase("endfunc")))) {
                break; //nextToken?? todo
            }

            assertTokenType(OperatorToken.TYPE);
            assertTokenBody(";");
            nextToken();
        }

        return body;
    }

    private AstOperation parseOperation() throws SyntaxException, BadSyntaxException {
        if (currentToken.getType().equalsIgnoreCase(IdentifierToken.TYPE)) {
            // variable_definition | variable_assigment | function_call
            Token t = currentToken;
            nextToken();
            if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase("(")) {
                backlog = currentToken;
                currentToken = t;
                return parseFunctionCall(); //todo check currentToken
            } else {
                backlog = currentToken;
                currentToken = t;
                return new AstVariableInitialization(parseVariableDefinitionOrInitialization()); //todo check currentToken
            }
        } else if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)) {
            //conditional_block | while_block | for_block | repeat_block | exception_block.
        } else {
            //throw?
        }
    }

    private AstFunctionCallOperation parseFunctionCall() {

    }

    private AstFunctionHeader parseFunctionHeader(boolean isProcedure) throws SyntaxException, BadSyntaxException {
        nextToken();
        assertTokenType(IdentifierToken.TYPE);
        Token name = currentToken;

        nextToken();
        assertTokenType(OperatorToken.TYPE);
        assertTokenBody("(");

        AstFunctionHeader header = new AstFunctionHeader(name.getBody());

        nextToken();
        if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                && currentToken.getBody().equalsIgnoreCase(")")) {
            return header;
        }

        Set<AstVariable> set = new HashSet<>();
        for (;;) {
            set.addAll(parseVariableDefinition());
            assertTokenType(OperatorToken.TYPE);

            if (currentToken.getBody().equalsIgnoreCase(")")) {
                break;
            }

            assertTokenBody(";");
        }
        header.setVariables(set);
        nextToken();

        if (!isProcedure) {
            assertTokenType(OperatorToken.TYPE);
            assertTokenBody("->");
            nextToken();
            AstType type = parseType();
            header.setReturnType(type);
        }
        return header;
    }

    private Set<AstVariable> parseVariableDefinition() throws SyntaxException, BadSyntaxException {
        //no initialization
        Set<AstVariable> set = new HashSet<>();
        for (;;) {
            assertTokenType(IdentifierToken.TYPE);
            String name = currentToken.getBody();
            nextToken();

            assertTokenType(OperatorToken.TYPE);
            if (currentToken.getBody().equalsIgnoreCase("->")) {
                break;
            } else if (!currentToken.getBody().equalsIgnoreCase(",")) {
                throw new BadSyntaxException("Bad syntax in variable definition: expected , or ->");
            }

            set.add(new AstVariable(new AstIdentExpr(name)));
            nextToken();
        }
        nextToken();

        AstType type = parseType();
        for (AstVariable variable : set) {
            variable.setType(type);
        }

        return set;
    }

    private Set<AstVariable> parseVariableDefinitionOrInitialization() throws SyntaxException, BadSyntaxException {
        //initialization or definition
        Set<AstVariable> set = new HashSet<>();
        for (;;) {
            AstIdentExpr expr = parseIdentExpr();
            nextToken();

            assertTokenType(OperatorToken.TYPE);
            if (currentToken.getBody().equalsIgnoreCase("->")) {
                break;
            } else if (currentToken.getBody().equalsIgnoreCase("=")) {
                //initialization
                nextToken();
                RValue rValue = parseRValue();
                set.add(new AstVariable(expr, rValue));
                nextToken();
                continue;
            } else if (!currentToken.getBody().equalsIgnoreCase(",")) {
                throw new BadSyntaxException("Bad syntax in variable definition: expected , or ->");
            }

            set.add(new AstVariable(expr));
            nextToken();
        }
        nextToken();

        AstType type = parseType();
        for (AstVariable variable : set) {
            variable.setType(type);
        }

        return set;
    }

    private AstType parseType() throws SyntaxException {
        int depth = 0;

        while (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase("[")) {
            depth++;
            nextToken();
        }

        assertTokenType(KeywordToken.TYPE);
        if (currentToken.getBody().equalsIgnoreCase("int")
                || currentToken.getBody().equalsIgnoreCase("bool")
                || currentToken.getBody().equalsIgnoreCase("char")) {
            String typeName = currentToken.getBody();
            nextToken();

            for (int i = depth; i > 0; i--) {
                assertTokenType(OperatorToken.TYPE);
                assertTokenBody("]");
                nextToken();
            }

            if (depth > 0) {
                return new AstType(typeName, true, depth);
            } else return new AstType(typeName, false, 0);
        } else {
            assertTokenBody("int or bool or char"); //guaranteed exception
            return null;
        }
    }

    RValue parseRValue() {
        return null; //todo
    }

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
