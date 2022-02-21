package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstType;

public class AstTypeInit implements RValue {
    private final AstType type;
    private final RValue size;
    private final int depth;

    public AstTypeInit(AstType type, RValue size, int depth) {
        this.type = type;
        this.size = size;
        this.depth = depth;
    }

    public static String TYPE = "TYPE_INITIALIZATION";

    @Override
    public String getType() {
        return null;
    }
}
