package ru.bmstu.iu9.personalfebus.compiler.ast.value;

public class AstArithExprSeparator implements AstArithExprPart {
    /**
     * 2 типа -
     * SEPARATOR_OPEN
     * SEPARATOR_CLOSE
     */
    private String type;

    public AstArithExprSeparator(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}
