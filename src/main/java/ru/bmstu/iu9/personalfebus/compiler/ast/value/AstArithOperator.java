package ru.bmstu.iu9.personalfebus.compiler.ast.value;

public class AstArithOperator implements AstArithExprPart {
    private final char operator;

    private final String subType;
    private final int priority; // - 2 | ^ 3 | * / % 4 | + - 5
    private final boolean isRightAssociative; //left = 0, right

    private static final String TYPE = "OPERATOR";

    public AstArithOperator(char operator, int unaryBinaryEtc) {
        this.operator = operator;

        if (unaryBinaryEtc == 1) subType = "UNARY_OPERATOR";
        else if (unaryBinaryEtc == 2) subType = "BINARY_OPERATOR";
        else subType = "TERNARY_OPERATOR";

        switch (operator) {
            case '-': {
                if (subType.equals("UNARY_OPERATOR")) {
                    priority = 2;
                    isRightAssociative = true;
                } else {
                    isRightAssociative = false;
                    priority = 5;
                }
                break;
            }
            case '^': {
                isRightAssociative = true;
                priority = 3;
                break;
            }
            case '*':
            case '/':
            case '%': {
                priority = 4;
                isRightAssociative = false;
                break;
            }
            case '+': {
                priority = 5;
                isRightAssociative = false;
                break;
            }
            default: {
                priority = 9;
                isRightAssociative = false;
            }
        }
    }

    public int getPriority() {
        return priority;
    }

    public boolean isRightAssociative() {
        return isRightAssociative;
    }

    public char getOperator() {
        return operator;
    }

    public String getSubType() {
        return subType;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
