package ru.bmstu.iu9.personalfebus.compiler.ast.operation.condition;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AstCondition {
    /**
     * выражение из операторов и значений в обратной польской нотации
     */
    private List<AstConditionPart> parts;
    private Deque<AstConditionPart> stack;

    private static String TYPE = "CONDITION";

    public AstCondition() {
        parts = new ArrayList<>();
        stack = new ArrayDeque<>();
    }

    public void addPart(AstConditionPart part) throws BadConditionExpressionException {
        if (part.getType().equals(AstConditionOperator.TYPE)) {
            AstConditionOperator operator = (AstConditionOperator) part;

            if (operator.getSubType().equals("UNARY_OPERATOR")) {
                stack.push(part);
            } else if (operator.getSubType().equals("BINARY_OPERATOR")) {
                while (stack.peekLast() != null && stack.peekLast().getType().equals(AstConditionOperator.TYPE)) {
                    AstConditionOperator operator2 = (AstConditionOperator)stack.peekLast();
                    if (operator2.getPriority() < operator.getPriority()
                            || (operator2.getPriority() == operator.getPriority() && !operator.isRightAssociative())) {
                        parts.add(stack.pop());
                    } else break;
                }
                stack.push(part);
            } else {
                //WTF TODO
                throw new BadConditionExpressionException();
            }
        } else if (part.getType().equals("CONDITION")) {
            System.out.println("WTF");
            throw new BadConditionExpressionException();
        } else if (part.getType().equals("SEPARATOR_OPEN")) {
            stack.push(part);
        } else if (part.getType().equals("SEPARATOR_CLOSE")) {
            while (stack.peekLast() != null && !stack.peekLast().getType().equals("SEPARATOR_OPEN")) {
                parts.add(stack.pop());
            }

            if (stack.peekLast() == null) {
                System.out.println("NOT CLOSED SEPARATORS");
                throw new BadConditionExpressionException();
            }

            stack.pop();
        } else {
            parts.add(part);
        }
    }

    public void emptyStack() throws BadConditionExpressionException {
        while (!stack.isEmpty()) {
            if (!stack.peekLast().getType().equals(AstConditionOperator.TYPE)) {
                throw new BadConditionExpressionException();
            }
            else parts.add(stack.pop());
        }
    }
}
