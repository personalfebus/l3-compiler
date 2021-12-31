package ru.bmstu.iu9.personalfebus.compiler.ast;

import java.util.ArrayList;
import java.util.List;

public class AstProgram {
    private List<AstFunction> functions;
    private AstFunction main;

    public AstProgram() {
        this.functions = new ArrayList<>();
    }

    public void setMain(AstFunction main) {
        this.main = main;
    }

    public void addFunction(AstFunction function) {
        functions.add(function);
    }
}
