package ru.bmstu.iu9.personalfebus.compiler.parser;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunctionBody;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunctionHeader;
import ru.bmstu.iu9.personalfebus.compiler.ast.AstProgram;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.*;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.block.*;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition.*;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.model.InitializationOrAssigment;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.*;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstType;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.lexer.ILexer;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.*;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.stream.Stream;

public class Parser implements IParser {
    private final ILexer lexer;
    private Token currentToken;
    private Token backlog;

    public Parser(ILexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public AstProgram parse() throws SyntaxException, BadArithmeticExpressionException, BadSyntaxException, NotAnArithmeticExpressionError, BadConditionExpressionException, AlreadyDeclaredException {
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

    private AstProgram parseProgram() throws SyntaxException, BadSyntaxException, BadArithmeticExpressionException, NotAnArithmeticExpressionError, BadConditionExpressionException, AlreadyDeclaredException {
        nextToken();
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

    private AstFunction parseFunction() throws SyntaxException, BadSyntaxException, BadArithmeticExpressionException, NotAnArithmeticExpressionError, BadConditionExpressionException, AlreadyDeclaredException {
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
            //conditional_block | while_block | for_block | repeat_block | exception_block.
            if (currentToken.getBody().equalsIgnoreCase("for")) {
                nextToken();
                return parseFor();
            } else if (currentToken.getBody().equalsIgnoreCase("while")) {
                nextToken();
                return parseWhile();
            } else if (currentToken.getBody().equalsIgnoreCase("if")) {
                nextToken();
                AstConditionalBlock block = new AstConditionalBlock();
                return parseIf(block);
            } else if (currentToken.getBody().equalsIgnoreCase("repeat")) {
                nextToken();
                return parseRepeat();
            } else if (currentToken.getBody().equalsIgnoreCase("check")) {
                nextToken();
                return parseCheck();
            }
            throw new BadSyntaxException("Unexpected keyword token at (" + currentToken.getLine() + "," + currentToken.getPosition() + ") in operation declaration (" + currentToken.getBody() + ")");
        } else {
            //throw
            throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + ") in operation definition idk (" + currentToken.getBody() + ")");
        }
    }

    private AstCheckBlock parseCheck() throws BadArithmeticExpressionException, SyntaxException, BadSyntaxException, NotAnArithmeticExpressionError, BadConditionExpressionException {
        AstCondition condition = new AstCondition();
        parseCondition(condition);
        condition.emptyStack();
        return new AstCheckBlock(condition);
    }

    private AstRepeatBlock parseRepeat() throws BadArithmeticExpressionException, SyntaxException, BadSyntaxException, NotAnArithmeticExpressionError, BadConditionExpressionException {
        List<AstOperation> operations = new ArrayList<>();

        if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)
                && currentToken.getBody().equalsIgnoreCase("until")) {
            nextToken();
            AstCondition condition = new AstCondition();
            parseCondition(condition);
            condition.emptyStack();
            return new AstRepeatBlock(condition, operations);
        }

        for (;;) {
            operations.add(parseOperation());
            //assuming parseOperation has nextToken in the end

            if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase("until")) {
                nextToken();
                break;
            }

            assertTokenType(OperatorToken.TYPE);
            assertTokenBody(";");
            nextToken();
        }

        AstCondition condition = new AstCondition();
        parseCondition(condition);
        condition.emptyStack();
        return new AstRepeatBlock(condition, operations);
    }

    private AstConditionalBlock parseIf(AstConditionalBlock block) throws BadConditionExpressionException, BadArithmeticExpressionException, SyntaxException, BadSyntaxException, NotAnArithmeticExpressionError {
        AstCondition condition = new AstCondition();
        parseCondition(condition);
        condition.emptyStack();
        assertTokenType(KeywordToken.TYPE);
        assertTokenBody("then");
        nextToken();

        List<AstOperation> body = new ArrayList<>();
        for (;;) {
            body.add(parseOperation());
            //assuming parseOperation has nextToken in the end

            if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)) {
                if (currentToken.getBody().equalsIgnoreCase("endif")) {
                    block.addBlock(new AstConditionalSubBlock(condition, body));
                    nextToken();
                    break;
                } else if (currentToken.getBody().equalsIgnoreCase("elseif")) {
                    block.addBlock(new AstConditionalSubBlock(condition, body));
                    nextToken();
                    parseIf(block);
                    break;
                } else if (currentToken.getBody().equalsIgnoreCase("else")) {
                    block.addBlock(new AstConditionalSubBlock(condition, body));
                    nextToken();
                    parseElse(block);
                    break;
                }
            }

            assertTokenType(OperatorToken.TYPE);
            assertTokenBody(";");
            nextToken();
        }

        return block;
    }

    private void parseElse(AstConditionalBlock block) throws SyntaxException, BadArithmeticExpressionException, BadSyntaxException, NotAnArithmeticExpressionError, BadConditionExpressionException {
        AstCondition condition = null;
        List<AstOperation> body = new ArrayList<>();
        for (;;) {
            body.add(parseOperation());
            //assuming parseOperation has nextToken in the end

            if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)) {
                if (currentToken.getBody().equalsIgnoreCase("endif")) {
                    block.addBlock(new AstConditionalSubBlock(condition, body));
                    nextToken();
                    break;
                }
            }

            assertTokenType(OperatorToken.TYPE);
            assertTokenBody(";");
            nextToken();
        }
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
            } else throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + ") in boolean compare expression: expected > < == != >= <= got token (" + currentToken.getBody() + ")");
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

    private AstWhileBlock parseWhile() throws BadArithmeticExpressionException, SyntaxException, BadSyntaxException, NotAnArithmeticExpressionError, BadConditionExpressionException {
        AstCondition condition = new AstCondition();
        parseCondition(condition);
        condition.emptyStack();
        assertTokenType(KeywordToken.TYPE);
        assertTokenBody("do");
        nextToken();
        List<AstOperation> operations = new ArrayList<>();

        if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)
                && currentToken.getBody().equalsIgnoreCase("endwhile")) {
            nextToken();
            return new AstWhileBlock(condition, operations);
        }

        for (;;) {
            operations.add(parseOperation());
            //assuming parseOperation has nextToken in the end

            if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)
                    && currentToken.getBody().equalsIgnoreCase("endwhile")) {
                nextToken();
                break;
            }

            assertTokenType(OperatorToken.TYPE);
            assertTokenBody(";");
            nextToken();
        }

        return new AstWhileBlock(condition, operations);
    }

    private AstForBlock parseFor() throws SyntaxException, BadArithmeticExpressionException, BadSyntaxException, NotAnArithmeticExpressionError, BadConditionExpressionException {
        InitializationOrAssigment ioa = parseVariableDefinitionOrInitialization();
        assertTokenType(KeywordToken.TYPE);
        assertTokenBody("to");
        nextToken();
        RValue to = parseRValue();

        assertTokenType(KeywordToken.TYPE);
        AstArithExpr commonStepExpr =  new AstArithExpr();
        commonStepExpr.addPart(new AstArithExprConstant("1"));
        RValue step = commonStepExpr;
        if (currentToken.getBody().equalsIgnoreCase("step")) {
            nextToken();
            step = parseRValue();
        }
        assertTokenType(KeywordToken.TYPE);
        assertTokenBody("do");
        nextToken();
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

    private AstFunctionHeader parseFunctionHeader(boolean isProcedure) throws SyntaxException, BadSyntaxException, AlreadyDeclaredException {
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
            Set<AstVariable> variables = parseVariableDefinition();
            if (variables.stream().anyMatch(set::contains)) {
                throw new AlreadyDeclaredException("Variable");
            }
            set.addAll(variables);
            assertTokenType(OperatorToken.TYPE);

            if (currentToken.getBody().equalsIgnoreCase(")")) {
                break;
            }

            assertTokenBody(";");
            nextToken();
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

    private Set<AstVariable> parseVariableDefinition() throws SyntaxException, BadSyntaxException, AlreadyDeclaredException {
        //no initialization
        Set<AstVariable> set = new HashSet<>();
        for (;;) {
            assertTokenType(IdentifierToken.TYPE);
            String name = currentToken.getBody();
            nextToken();

            assertTokenType(OperatorToken.TYPE);
            if (currentToken.getBody().equalsIgnoreCase("->")) {
                AstVariable newVariable = new AstVariable(new AstIdentExpr(name));
                if (set.contains(newVariable)) throw new AlreadyDeclaredException("Variable " + name);
                set.add(newVariable);
                break;
            } else if (!currentToken.getBody().equalsIgnoreCase(",")) {
                throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + ") in variable definition: expected , or -> got token (" + currentToken.getBody() + ")");
            }

            AstVariable newVariable = new AstVariable(new AstIdentExpr(name));
            if (set.contains(newVariable)) throw new AlreadyDeclaredException("Variable " + name);
            set.add(newVariable);
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

            if (currentToken.getBody().equalsIgnoreCase("endfunc")
                    || currentToken.getBody().equalsIgnoreCase("endproc")
                    || currentToken.getBody().equalsIgnoreCase("then")
                    || currentToken.getBody().equalsIgnoreCase("do")
                    || currentToken.getBody().equalsIgnoreCase("endif")
                    || currentToken.getBody().equalsIgnoreCase("elseif")
                    || currentToken.getBody().equalsIgnoreCase("else")
                    || currentToken.getBody().equalsIgnoreCase("endfor")
                    || currentToken.getBody().equalsIgnoreCase("endwhile")
                    || currentToken.getBody().equalsIgnoreCase("until")
                    || currentToken.getBody().equalsIgnoreCase(";")) {
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

        if (type.isArray()) throw new BadSyntaxException("Bad syntax at (" + currentToken.getLine() + "," + currentToken.getPosition() + ") in type initialization: expected basic type got array of " + type.getTypeName());

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
            constant.setSubType("string");
            arithExpr.addPart(constant);
            nextToken();
        } else if (currentToken.getType().equalsIgnoreCase(SymbolToken.TYPE)) {
            //symbol
            AstArithExprConstant constant = new AstArithExprConstant(currentToken.getBody());
            constant.setSubType("char");
            arithExpr.addPart(constant);
            nextToken();
        } else if (currentToken.getType().equalsIgnoreCase(KeywordToken.TYPE)) {
            if (currentToken.getBody().equalsIgnoreCase("ff")
                    || currentToken.getBody().equalsIgnoreCase("tt")) {
                //boolean constants
                AstArithExprConstant constant = new AstArithExprConstant(currentToken.getBody());
                constant.setSubType("bool");
                arithExpr.addPart(constant);
                nextToken();
            } else if (currentToken.getBody().equalsIgnoreCase("endfunc")
                    || currentToken.getBody().equalsIgnoreCase("endproc")
                    || currentToken.getBody().equalsIgnoreCase("then")
                    || currentToken.getBody().equalsIgnoreCase("do")
                    || currentToken.getBody().equalsIgnoreCase("endif")
                    || currentToken.getBody().equalsIgnoreCase("elseif")
                    || currentToken.getBody().equalsIgnoreCase("else")
                    || currentToken.getBody().equalsIgnoreCase("endfor")
                    || currentToken.getBody().equalsIgnoreCase("endwhile")
                    || currentToken.getBody().equalsIgnoreCase("until")) {
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
                return;
//                try {
//                    parseArithExpr(arithExpr);
//                    arithExpr.addPart(new AstArithOperator('*', 2));
//                    return;
//                } catch (NotAnArithmeticExpressionError er) {
//                    //end of arith expr = good
//                    return;
//                }
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

                if (currentToken.getType().equalsIgnoreCase(NumberToken.TYPE)
                    || currentToken.getType().equalsIgnoreCase(IdentifierToken.TYPE)) {
                    expr.addTail(currentToken.getBody());
                } else {
                    //expected number or ident
                    assertTokenType(NumberToken.TYPE);
                }
                nextToken();

                assertTokenBody("]");
                nextToken();
            } else break;
        }

        return expr;
    }
}
