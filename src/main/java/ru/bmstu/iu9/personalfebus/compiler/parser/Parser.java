package ru.bmstu.iu9.personalfebus.compiler.parser;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunctionBody;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunctionHeader;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstProgram;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.*;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.block.AstConditionalBlock;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.block.AstConditionalSubBlock;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.block.AstForBlock;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.block.AstWhileBlock;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition.*;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.model.InitializationOrAssigment;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.*;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstType;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.lexer.ILexer;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.*;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadSyntaxException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.NotAnArithmeticExpressionError;
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
    public AstProgram parse() throws SyntaxException, BadArithmeticExpressionException, BadSyntaxException, NotAnArithmeticExpressionError, BadConditionExpressionException {
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
            throw new SyntaxException(type, currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private void assertTokenBody(String body) throws SyntaxException {
        if (!currentToken.getBody().equalsIgnoreCase(body)) {
            throw new SyntaxException(body, currentToken.getBody(), currentToken.getLine(), currentToken.getPosition());
        }
    }

    private AstProgram parseProgram() throws SyntaxException, BadSyntaxException, BadArithmeticExpressionException, NotAnArithmeticExpressionError, BadConditionExpressionException {
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

    private AstFunction parseFunction() throws SyntaxException, BadSyntaxException, BadArithmeticExpressionException, NotAnArithmeticExpressionError, BadConditionExpressionException {
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
            throw new BadSyntaxException("Bad syntax in function header: expected func or proc or main at (" + currentToken.getBody() + ")");
        }
    }

    private AstFunctionBody parseFunctionBody(boolean isProcedure) throws SyntaxException, BadSyntaxException, BadArithmeticExpressionException, NotAnArithmeticExpressionError, BadConditionExpressionException {
        AstFunctionBody body = new AstFunctionBody();

        //todo procedure - done?
        //empty function body case
        if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)
                && ((isProcedure && currentToken.getBody().equalsIgnoreCase("endproc"))
                || (!isProcedure && currentToken.getBody().equalsIgnoreCase("endfunc")))) {
            nextToken();
            return body;
        }

        for (;;) {
            body.addOperation(parseOperation());
            //assuming parseOperation has nextToken in the end

            if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)
                    && ((isProcedure && currentToken.getBody().equalsIgnoreCase("endproc"))
                        || (!isProcedure && currentToken.getBody().equalsIgnoreCase("endfunc")))) {
                nextToken();
                break;
            }

            assertTokenType(OperatorToken.TYPE);
            assertTokenBody(";");
            nextToken();
        }

        return body;
    }

    private AstOperation parseOperation() throws SyntaxException, BadSyntaxException, BadArithmeticExpressionException, NotAnArithmeticExpressionError, BadConditionExpressionException {
        if (currentToken.getType().equalsIgnoreCase(IdentifierToken.TYPE)) {
            // variable_definition | variable_assigment | function_call
            Token t = currentToken;
            nextToken();
            if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase("(")) {
                backlog = currentToken;
                currentToken = t;
                return parseFunctionCall();
            } else {
                backlog = currentToken;
                currentToken = t;
                InitializationOrAssigment ioa = parseVariableDefinitionOrInitialization();

                if (ioa.isInit) {
                    return new AstVariableInitialization(ioa.variables);
                } else {
                    return new AstVariableAssigment(ioa.variables);
                }
            }
        } else if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)) {
            //conditional_block | while_block | for_block | repeat_block | exception_block. todo
            if (currentToken.getBody().equalsIgnoreCase("for")) {
                nextToken();
                return parseFor();
            } else if (currentToken.getBody().equalsIgnoreCase("while")) {
                return parseWhile();
            } else if (currentToken.getBody().equalsIgnoreCase("if")) {
                nextToken();
                AstConditionalBlock block = new AstConditionalBlock();
                return parseIf(block);
            }
            assertTokenType(IdentifierToken.TYPE);
            throw new BadSyntaxException("TODO1");
        } else {
            //throw? todo
            throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + ") in operation definition idk (" + currentToken.getBody() + ")");
        }
    }

    //todo tests
    private AstConditionalBlock parseIf(AstConditionalBlock block) throws BadConditionExpressionException, BadArithmeticExpressionException, SyntaxException, BadSyntaxException, NotAnArithmeticExpressionError {
        AstCondition condition = new AstCondition();
        parseCondition(condition);
        condition.emptyStack();
        assertTokenType(KeywordToken.TYPE);
        assertTokenBody("then");

        List<AstOperation> body = new ArrayList<>();
        for (;;) {
            body.add(parseOperation());
            //assuming parseOperation has nextToken in the end

            if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)) {
                if (currentToken.getBody().equalsIgnoreCase("endif")) {
                    block.addBlock(new AstConditionalSubBlock(condition, body));
                    nextToken();
                    break;
                } else if (currentToken.getBody().equalsIgnoreCase("elseif")
                        || currentToken.getBody().equalsIgnoreCase("else")) {
                    block.addBlock(new AstConditionalSubBlock(condition, body));
                    nextToken();
                    parseIf(block);
                    break;
                }
            }

            assertTokenType(OperatorToken.TYPE);
            assertTokenBody(";");
            nextToken();
        }

        return block;
    }

    private void parseCondition(AstCondition condition) throws BadConditionExpressionException, BadArithmeticExpressionException, SyntaxException, BadSyntaxException, NotAnArithmeticExpressionError {
        if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                && currentToken.getBody().equalsIgnoreCase("!")) {
            condition.addPart(new AstConditionOperator("!"));
            nextToken();
        }

        parseBooleanValue(condition);
        parseConditionTail(condition);
    }

    private void parseBooleanValue(AstCondition condition) throws BadConditionExpressionException, BadArithmeticExpressionException, SyntaxException, BadSyntaxException, NotAnArithmeticExpressionError {
        if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)) {
            if (currentToken.getBody().equalsIgnoreCase("ff")
                    || currentToken.getBody().equalsIgnoreCase("tt")) {
                //boolean constants
                condition.addPart(new AstConditionConstant(currentToken.getBody()));
                nextToken();
            }
        } else {
            RValue left = parseRValue();
            condition.addPart(new AstConditionConstantRValue(left));
            assertTokenType(OperatorToken.TYPE);
            // > < == != >= <=
            if (currentToken.getBody().equalsIgnoreCase("<") || currentToken.getBody().equalsIgnoreCase(">")
                || currentToken.getBody().equalsIgnoreCase("<=") || currentToken.getBody().equalsIgnoreCase(">=")
                || currentToken.getBody().equalsIgnoreCase("==") || currentToken.getBody().equalsIgnoreCase("!=")) {
                condition.addPart(new AstConditionOperator(currentToken.getBody()));
                nextToken();
                RValue right = parseRValue();
                condition.addPart(new AstConditionConstantRValue(right));
            } else throw new BadSyntaxException("TODO"); //TODO
        }
    }

    private void parseConditionTail(AstCondition condition) throws BadSyntaxException, BadConditionExpressionException, BadArithmeticExpressionException, SyntaxException, NotAnArithmeticExpressionError {
        if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)) {
            if (currentToken.getBody().equalsIgnoreCase("!")
                || currentToken.getBody().equalsIgnoreCase("&&")
                || currentToken.getBody().equalsIgnoreCase("||")
                || currentToken.getBody().equalsIgnoreCase("^^")) {
                // ! && ^^ ||
                condition.addPart(new AstConditionOperator(currentToken.getBody()));
                nextToken();
                parseCondition(condition);
            }
        }
    }

    //todo + condition
    private AstWhileBlock parseWhile() {
        return null;
    }

    private AstForBlock parseFor() throws SyntaxException, BadArithmeticExpressionException, BadSyntaxException, NotAnArithmeticExpressionError, BadConditionExpressionException {
        InitializationOrAssigment ioa = parseVariableDefinitionOrInitialization();
        assertTokenType(KeywordToken.TYPE);
        assertTokenBody("to");
        nextToken();
        RValue to = parseRValue();
        assertTokenType(KeywordToken.TYPE);
        assertTokenBody("step");
        nextToken();
        RValue step = parseRValue();
        assertTokenType(KeywordToken.TYPE);
        assertTokenBody("do");
        List<AstOperation> operations = new ArrayList<>();

        if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)
                && currentToken.getBody().equalsIgnoreCase("endfor")) {
            nextToken();
            return new AstForBlock(ioa, to, step, operations);
        }

        for (;;) {
            operations.add(parseOperation());
            //assuming parseOperation has nextToken in the end

            if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase("endfor")) {
                nextToken();
                break;
            }

            assertTokenType(OperatorToken.TYPE);
            assertTokenBody(";");
            nextToken();
        }

        return new AstForBlock(ioa, to, step, operations);
    }

    private AstFunctionCallOperation parseFunctionCall() throws SyntaxException, BadArithmeticExpressionException, BadSyntaxException, NotAnArithmeticExpressionError {
        assertTokenType(IdentifierToken.TYPE);
        Token name = currentToken;
        nextToken();

        assertTokenType(OperatorToken.TYPE);
        assertTokenBody("(");

        AstFunctionCallOperation functionCallOperation = new AstFunctionCallOperation(name.getBody());
        nextToken();

        for (;;) {
            if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase(")")) {
                nextToken();
                break;
            }

            RValue rValue = parseRValue();
            functionCallOperation.addArgument(rValue);
        }

        return functionCallOperation;
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
                throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + ") in variable definition: expected , or -> got token (" + currentToken.getBody() + ")");
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

    private InitializationOrAssigment parseVariableDefinitionOrInitialization() throws SyntaxException, BadSyntaxException, BadArithmeticExpressionException, NotAnArithmeticExpressionError {
        //initialization or definition
        Set<AstVariable> set = new HashSet<>();
        for (;;) {
            AstIdentExpr expr = parseIdentExpr();

            assertTokenType(OperatorToken.TYPE);
            boolean alreadyAdded = false;

            if (currentToken.getBody().equalsIgnoreCase("=")) {
                //initialization
                nextToken();
                RValue rValue = parseRValue();
                set.add(new AstVariable(expr, rValue));
                alreadyAdded = true;
            }

            if (!alreadyAdded) {
                set.add(new AstVariable(expr));
            }

            if (currentToken.getBody().equalsIgnoreCase("->")) {
                break;
            }

            if (currentToken.getBody().equalsIgnoreCase(";")
                    || currentToken.getBody().equalsIgnoreCase("endfunc")
                    || currentToken.getBody().equalsIgnoreCase("endproc")) {
                return new InitializationOrAssigment(set, false);
            }

            if (currentToken.getBody().equalsIgnoreCase(",")) {
                nextToken();
            } else {
                throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + ") in variable definition: expected , or -> got token (" + currentToken.getBody() + ")");
            }

        }
        nextToken();

        AstType type = parseType();
        for (AstVariable variable : set) {
            variable.setType(type);
        }

        return new InitializationOrAssigment(set, true);
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

    private RValue parseRValue() throws BadSyntaxException, BadArithmeticExpressionException, SyntaxException, NotAnArithmeticExpressionError {
        if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                && currentToken.getBody().equalsIgnoreCase("[")) {
            return parseTypeInit();
        }
        AstArithExpr expr = new AstArithExpr();
        parseArithExpr(expr);
        expr.emptyStack();
        return expr;
    }

    private RValue parseTypeInit() throws SyntaxException, BadArithmeticExpressionException, BadSyntaxException, NotAnArithmeticExpressionError {
        assertTokenType(OperatorToken.TYPE);
        assertTokenBody("[");
        int depth = 0;

        while (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                && currentToken.getBody().equalsIgnoreCase("[")) {
            depth++;
            nextToken();
        }

        AstType type = parseType();
        RValue rValue = parseRValue();

        AstTypeInit init = new AstTypeInit(type, rValue, depth);

        for (int i = depth; i > 0; i--) {
            assertTokenType(OperatorToken.TYPE);
            assertTokenBody("]");
            nextToken();
        }

        return init;
    }

    private void parseArithExpr(AstArithExpr arithExpr) throws BadSyntaxException, BadArithmeticExpressionException, SyntaxException, NotAnArithmeticExpressionError {
        if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                && currentToken.getBody().equalsIgnoreCase("-")) {
            arithExpr.addPart(new AstArithOperator('-', 1));
            nextToken();
        }
        parseArithExprTail(arithExpr);
    }

    private void parseArithExprTail(AstArithExpr arithExpr) throws BadArithmeticExpressionException, BadSyntaxException, SyntaxException, NotAnArithmeticExpressionError {
        if (currentToken.getType().equalsIgnoreCase(StringToken.TYPE)) {
            //string
            AstArithExprConstant constant = new AstArithExprConstant(currentToken.getBody());
            arithExpr.addPart(constant);
            nextToken();
        } else if (currentToken.getType().equalsIgnoreCase(SymbolToken.TYPE)) {
            //symbol
            AstArithExprConstant constant = new AstArithExprConstant(currentToken.getBody());
            arithExpr.addPart(constant);
            nextToken();
        } else if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)) {
            if (currentToken.getBody().equalsIgnoreCase("ff")
                    || currentToken.getBody().equalsIgnoreCase("tt")) {
                //boolean constants
                AstArithExprConstant constant = new AstArithExprConstant(currentToken.getBody());
                arithExpr.addPart(constant);
                nextToken();
            } else if (currentToken.getBody().equalsIgnoreCase("endfunc")
                    || currentToken.getBody().equalsIgnoreCase("endproc")) {
                System.out.println("eeeeeHUH?");
                throw new NotAnArithmeticExpressionError();
            } else throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + "): unexpected keyword token (" + currentToken.getBody() + ")");
        } else if (currentToken.getType().equalsIgnoreCase(IdentifierToken.TYPE)) {
            //ident_expr or function_call
            Token t = currentToken;
            nextToken();
            if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase("(")) {
                backlog = currentToken;
                currentToken = t;
                AstFunctionCallOperation call = parseFunctionCall();
                AstArithExprFuncConstant constant = new AstArithExprFuncConstant(call.getName(), call.getArguments());
                arithExpr.addPart(constant);
            } else {
                backlog = currentToken;
                currentToken = t;
                AstIdentExpr expr = parseIdentExpr();
                AstArithExprIdentConstant constant = new AstArithExprIdentConstant(expr.getName(), expr.getTail());
                arithExpr.addPart(constant);
            }
        } else if (currentToken.getType().equalsIgnoreCase(NumberToken.TYPE)) {
            //NUMBER
            AstArithExprConstant constant = new AstArithExprConstant(currentToken.getBody());
            arithExpr.addPart(constant);
            nextToken();
        } else if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)) {
            if (currentToken.getBody().equalsIgnoreCase("(")) {
                // <(> arith_expr <)>
                arithExpr.addPart(new AstArithExprSeparator("SEPARATOR_OPEN"));
                nextToken();
                parseArithExpr(arithExpr);
                assertTokenType(OperatorToken.TYPE);
                assertTokenBody(")");
                arithExpr.addPart(new AstArithExprSeparator("SEPARATOR_CLOSE"));
                nextToken();
            } else if (currentToken.getBody().equalsIgnoreCase(";")
                    || currentToken.getBody().equalsIgnoreCase("->") ) {
                System.out.println("HUH?");
                throw new NotAnArithmeticExpressionError();
            } return;
            //else throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + "): unexpected operator token");
        } else {
            System.out.println("else)))");
            throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + "): unexpected token (" + currentToken.getBody() + ")");
        }

        parseRValueTail(arithExpr);
    }

    private void parseRValueTail(AstArithExpr arithExpr) throws BadArithmeticExpressionException, SyntaxException, BadSyntaxException, NotAnArithmeticExpressionError {
        if (currentToken.getType().equalsIgnoreCase(OperatorToken.TYPE)) {
            //binary op <^> | </> | <%> | <+> | <->.
            if (currentToken.getBody().equalsIgnoreCase("^")) {
                arithExpr.addPart(new AstArithOperator('^', 2));
            } else if (currentToken.getBody().equalsIgnoreCase("/")) {
                arithExpr.addPart(new AstArithOperator('/', 2));
            } else if (currentToken.getBody().equalsIgnoreCase("%")) {
                arithExpr.addPart(new AstArithOperator('%', 2));
            } else if (currentToken.getBody().equalsIgnoreCase("+")) {
                arithExpr.addPart(new AstArithOperator('+', 2));
            } else if (currentToken.getBody().equalsIgnoreCase("-")) {
                arithExpr.addPart(new AstArithOperator('-', 2));
            } else {
                try {
                    parseArithExpr(arithExpr);
                    arithExpr.addPart(new AstArithOperator('*', 2));
                    return;
                } catch (NotAnArithmeticExpressionError er) {
                    //end of arith expr = good
                    return;
                }
            }
            //else throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + "): unexpected operator token");

            nextToken();
            parseArithExpr(arithExpr);
        } else {
            try {
                parseArithExpr(arithExpr);
                arithExpr.addPart(new AstArithOperator('*', 2));
                return;
            } catch (NotAnArithmeticExpressionError er) {
                //end of arith expr = good
                return;
            }
        }
    }

    private AstIdentExpr parseIdentExpr() throws SyntaxException {
        assertTokenType(IdentifierToken.TYPE);
        AstIdentExpr expr = new AstIdentExpr(currentToken.getBody());
        nextToken();

        for (;;) {
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
