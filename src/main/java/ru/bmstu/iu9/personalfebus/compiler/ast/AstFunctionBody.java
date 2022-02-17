package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;

import java.util.ArrayList;
import java.util.List;

public class AstFunctionBody {
    private List<AstOperation> operations;

    public AstFunctionBody() {
        this.operations = new ArrayList<>();
    }

    public void addOperation(AstOperation operation) {
        this.operations.add(operation);
    }
}
