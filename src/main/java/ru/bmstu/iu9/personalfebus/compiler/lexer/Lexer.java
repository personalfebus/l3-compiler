package ru.bmstu.iu9.personalfebus.compiler.lexer;

import ru.bmstu.iu9.personalfebus.compiler.lexer.token.*;
import ru.bmstu.iu9.personalfebus.compiler.lexer.token.exception.*;

public class Lexer implements ILexer {
    private final String input;
    private int position;
    private int line;
    private int linePosition;
    private Token currentToken;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.linePosition = 1;
    }

    public Lexer(String input, int position) {
        this.input = input;
        this.position = position;
        this.line = 1;
        this.linePosition = position + 1;
    }

    @Override
    public boolean hasTokens() {
        if (position >= input.length()) {
            return false;
        } else {
            try {
                getNextToken();
                return currentToken != null;
            } catch (Exception ex) {
                System.out.println(ex);
                return hasTokens();
            }
        }
    }

    private void getNextToken() throws BadNumberSyntaxError, BadIdentifierSyntaxError, UnexpectedSymbolError, SymbolClosureError, SymbolInterpretationException, StringClosureError {
        while (position < input.length()) {
            //System.out.println(position + ") " + input.charAt(position));
            if (isSpaceSymbol()) {
                // skip space symbols
                moveForward();
            } else if (isOperator() > 0) {
                int len = isOperator();
                String op = input.substring(position, position + len);
                currentToken = new OperatorToken(line, linePosition, op);
                position += len;
                linePosition += len;
                return;
            } else if (isLetter()) {
                // lex keyword and identificators
                int start = position;
                skipToEndOfLexeme();
                String word = input.substring(start, position);
                if (isKeyword(word)) {
                    currentToken = new KeywordToken(line, linePosition, word);
                } else {
                    currentToken = new IdentifierToken(line, linePosition, word);
                }
                return;
            } else if (isDigit()) {
                // lex number
                int start = position;
                skipToEndOfLexeme();
                String number = input.substring(start, position);
                currentToken = new NumberToken(number, line, linePosition);
                return;
            } else if (isSingleQuote()) {
                int start = position + 1;
                skipToEndOfQuote(true);
                String symbol = input.substring(start, position);
                currentToken = new SymbolToken(line, linePosition, symbol);
                moveForward();
                return;
            } else if (isDoubleQuote()) {
                int start = position + 1;
                skipToEndOfQuote(false);
                String str = input.substring(start, position);
                currentToken = new StringToken(line, linePosition, str);
                moveForward();
                return;
            } else {
                //other symbols ???
                moveForward();
                throw new UnexpectedSymbolError(line, linePosition);
            }
        }
        currentToken = null;
    }

    private void skipToEndOfQuote(boolean isSingle) throws SymbolClosureError, StringClosureError {
        moveForward();
        int numberOfDollars = 0;
        if (isSingle) {
            for (;position < input.length();) {
                if (isDollar()) numberOfDollars++;
                else if (isSingleQuote() && numberOfDollars % 2 == 0) break;
                moveForward();
            }
            if (position >= input.length()) throw new SymbolClosureError(line, linePosition);
        } else {
            for (;position < input.length();) {
                if (isDollar()) numberOfDollars++;
                else if (isDoubleQuote() && numberOfDollars % 2 == 0) break;
                moveForward();
            }
            if (position >= input.length()) throw new StringClosureError(line, linePosition);
        }
    }

    private boolean isDollar() {
        return input.charAt(position) == '$';
    }

    private int isOperator() {
        //operators + separators
        if (position < input.length() - 1) {
            if ((input.charAt(position) == '-' && input.charAt(position + 1) == '>') ||
                    (input.charAt(position) == '=' && input.charAt(position + 1) == '=') ||
                    (input.charAt(position) == '!' && input.charAt(position + 1) == '=') ||
                    (input.charAt(position) == '<' && input.charAt(position + 1) == '=') ||
                    (input.charAt(position) == '>' && input.charAt(position + 1) == '=') ||
                    (input.charAt(position) == '|' && input.charAt(position + 1) == '|') ||
                    (input.charAt(position) == '&' && input.charAt(position + 1) == '&') ||
                    (input.charAt(position) == '^' && input.charAt(position + 1) == '^')) {
                return 2;
            }
        }
        if (input.charAt(position) == '=' || input.charAt(position) == '+' ||
            input.charAt(position) == '-' || input.charAt(position) == '/' ||
            input.charAt(position) == '%' || input.charAt(position) == '^' ||
            input.charAt(position) == '!' || input.charAt(position) == '<' ||
            input.charAt(position) == '>' || input.charAt(position) == '(' ||
            input.charAt(position) == ')' || input.charAt(position) == '[' ||
            input.charAt(position) == ']' || input.charAt(position) == ',' ||
            input.charAt(position) == ';') {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean isSingleQuote() {
        return input.charAt(position) == '\'';
    }

    private boolean isDoubleQuote() {
        return input.charAt(position) == '\"';
    }

    private boolean isKeyword(String word) {
        //case insensitive
        return word.equalsIgnoreCase("bool") ||
                word.equalsIgnoreCase("char") ||
                word.equalsIgnoreCase("check") ||
                word.equalsIgnoreCase("do") ||
                word.equalsIgnoreCase("else") ||
                word.equalsIgnoreCase("elseif") ||
                word.equalsIgnoreCase("endfor") ||
                word.equalsIgnoreCase("endfunc") ||
                word.equalsIgnoreCase("endif") ||
                word.equalsIgnoreCase("endproc") ||
                word.equalsIgnoreCase("endwhile") ||
                word.equalsIgnoreCase("ff") ||
                word.equalsIgnoreCase("for") ||
                word.equalsIgnoreCase("func") ||
                word.equalsIgnoreCase("if") ||
                word.equalsIgnoreCase("int") ||
                word.equalsIgnoreCase("nil") ||
                word.equalsIgnoreCase("proc") ||
                word.equalsIgnoreCase("repeat") ||
                word.equalsIgnoreCase("step") ||
                word.equalsIgnoreCase("then") ||
                word.equalsIgnoreCase("to") ||
                word.equalsIgnoreCase("tt") ||
                word.equalsIgnoreCase("until") ||
                word.equalsIgnoreCase("while");
    }

    private void skipToEndOfLexeme() {
        while (!isSpaceSymbol() &&  !(isOperator() > 0)) {
            moveForward();
        }
    }

    private boolean isLetter() {
        return (input.charAt(position) >= 'a' && input.charAt(position) <= 'z') ||
                (input.charAt(position) >= 'A' && input.charAt(position) <= 'Z') ||
                (input.charAt(position) == '_') || (input.charAt(position) == '?') ||
                (input.charAt(position) == '$') || (input.charAt(position) == '@') ||
                (input.charAt(position) == '.') || (input.charAt(position) == '#') ||
                (input.charAt(position) == '~');
    }

    private void moveForward() {
        checkForNewline();
        position++;
        linePosition++;
    }

    private void checkForNewline() {
        if (input.charAt(position) == '\n') {
            line++;
            linePosition = 0;
        }
    }

    private boolean isDigit() {
        return Character.isDigit(input.charAt(position));
    }

    private boolean isSpaceSymbol() {
        return input.charAt(position) == ' ' || input.charAt(position) == '\t' || input.charAt(position) == '\n';
    }

    @Override
    public Token nextToken() {
        return currentToken;
    }
}
