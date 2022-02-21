package ru.bmstu.iu9.personalfebus.compiler.ast.operation;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstArithExprPart;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;

import java.util.ArrayList;
import java.util.List;

public class AstFunctionCallOperation implements RValue, AstOperation {
    private final String name;
    private List<RValue> arguments;

    private static String TYPE = "FUNCTION_CALL";

    public AstFunctionCallOperation(String name) {
        this.name = name;
        arguments = new ArrayList<>();
    }

    public void addArgument(RValue value) {
        arguments.add(value);
    }

    public String getName() {
        return name;
    }

    public List<RValue> getArguments() {
        return arguments;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
