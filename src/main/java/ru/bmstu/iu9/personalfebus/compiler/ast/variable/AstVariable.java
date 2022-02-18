package ru.bmstu.iu9.personalfebus.compiler.ast.variable;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;

public class AstVariable {
    private final AstIdentExpr expr;
    private AstType type;
    private RValue rValue;

    public AstVariable(AstIdentExpr expr) {
        this.expr = expr;
    }

    public AstVariable(AstIdentExpr expr, RValue rValue) {
        this.expr = expr;
        this.rValue = rValue;
    }

    public boolean isInitialized() {
        return rValue != null;
    }

    public AstIdentExpr getExpr() {
        return expr;
    }

    public AstType getType() {
        return type;
    }

    public RValue getrValue() {
        return rValue;
    }

    public void setType(AstType type) {
        this.type = type;
    }

    public void setrValue(RValue rValue) {
        this.rValue = rValue;
    }
}
