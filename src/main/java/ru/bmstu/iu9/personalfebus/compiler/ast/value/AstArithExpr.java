package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import java.util.ArrayList;

//возможно стоит добавить проверку типов
public class AstArithExpr implements RValue, AstArithExprPart {
    /**
     * выражение из операторов и значений в обратной польской нотации
     */
    private ArrayList<AstArithExprPart> parts;

    private static String TYPE = "ARITHMETIC_EXPRESSION";

    public AstArithExpr() {
        this.parts = new ArrayList<>();
    }

    public void addPart(AstArithExprPart part) {
        this.parts.add(part);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
