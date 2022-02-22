package ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition;

public class AstConditionOperator implements AstConditionPart {
    private final String operator;
    private final int priority; // ! 2 | > < == != >= <= 6 | && 7 | || ^^ 8
    private final boolean isRightAssociative;
    private final String subType;

   public static final String TYPE = "CONDITION_OPERATOR";

    public AstConditionOperator(String operator) {
        this.operator = operator;

        switch (operator) {
            case "!": {
                priority = 2;
                isRightAssociative = true;
                subType = "UNARY_OPERATOR";
                break;
            }
            case ">":
            case "<":
            case "==":
            case "!=":
            case ">=":
            case "<=": {
                priority = 6;
                isRightAssociative = false;
                subType = "BINARY_OPERATOR";
                break;
            }
            case "&&": {
                priority = 7;
                isRightAssociative = false;
                subType = "BINARY_OPERATOR";
                break;
            }
            case "||":
            case "^^": {
                priority = 8;
                isRightAssociative = false;
                subType = "BINARY_OPERATOR";
                break;
            }
            default: {
                //WTF
                priority = 9;
                isRightAssociative = false;
                subType = "BINARY_OPERATOR";
            }
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public String getSubType() {
        return subType;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isRightAssociative() {
        return isRightAssociative;
    }
}
