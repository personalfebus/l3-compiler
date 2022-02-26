package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.generator.Generatable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.AlreadyDeclaredException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AstProgram implements Generatable {
    private Set<AstFunction> functions;
    private boolean hasMain;

    public AstProgram() {
        this.functions = new HashSet<>();
        this.hasMain = false;
    }

    public void addFunction(AstFunction function) throws AlreadyDeclaredException {
        if (function.getHeader().getName().equalsIgnoreCase("main")) {
            if (!function.getHeader().getReturnType().isArray() && function.getHeader().getReturnType().getTypeName().equalsIgnoreCase("int")) {
                if (function.getHeader().getVariables().size() == 1) {
                    function.getHeader().getVariables().forEach(v -> {
                        if (v.getType().getTypeName().equalsIgnoreCase("char") && (v.getType().getArrayDepth() == 2)) {
                            hasMain = true;
                        }
                    });
                }
            }
        }

        if (functions.contains(function)) {
            throw new AlreadyDeclaredException("Function " + function.getHeader().getName());
        }

        functions.add(function);
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper) throws MissingException {
        if (!hasMain) {
            throw new MissingException("Function main");
        }

        declaredFunctions = functions;
        StringBuilder generatedCode = new StringBuilder();
        for (AstFunction function : functions) {
            generatedCode.append(function.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper));
        }

        return generatedCode.toString();
    }
}
