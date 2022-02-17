package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

//возможно стоит добавить проверку типов
public class AstArithExpr implements RValue, AstArithExprPart {
    /**
     * выражение из операторов и значений в обратной польской нотации
     */
    private List<AstArithExprPart> parts;
    private Deque<AstArithExprPart> stack;

    private static String TYPE = "ARITHMETIC_EXPRESSION";

    public AstArithExpr() {
        this.parts = new ArrayList<>();
        this.stack = new ArrayDeque<>();
    }

    public void addPart(AstArithExprPart part) throws BadArithmeticExpressionException {
        if (part.getType().equals("OPERATOR")) {
            AstArithOperator operator = (AstArithOperator)part;

            if (operator.getSubType().equals("UNARY_OPERATOR")) {
                stack.push(part);
            } else if (operator.getSubType().equals("BINARY_OPERATOR")) {
                while (stack.peekLast() != null && stack.peekLast().getType().equals("OPERATOR")) {
                    AstArithOperator operator2 = (AstArithOperator)stack.peekLast();
                    if (operator2.getPriority() < operator.getPriority()
                        || (operator2.getPriority() == operator.getPriority() && !operator.isRightAssociative())) {
                        parts.add(stack.pop());
                    } else break;
                }
                stack.push(part);
            } else {
                //WTF TODO
                throw new BadArithmeticExpressionException();
            }
        } else if (part.getType().equals("ARITHMETIC_EXPRESSION")) {
            System.out.println("WTF");
            throw new BadArithmeticExpressionException();
        } else if (part.getType().equals("SEPARATOR_OPEN")) {
            stack.push(part);
        } else if (part.getType().equals("SEPARATOR_CLOSE")) {
            while (stack.peekLast() != null && !stack.peekLast().getType().equals("SEPARATOR_OPEN")) {
                parts.add(stack.pop());
            }

            if (stack.peekLast() == null) {
                System.out.println("NOT CLOSED SEPARATORS");
                throw new BadArithmeticExpressionException();
            }

            stack.pop();
        } else {
            parts.add(part);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
