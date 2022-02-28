package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.generator.Generatable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.AlreadyDeclaredException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class AstFunction implements Generatable {
    private final AstFunctionHeader header;
    private final AstFunctionBody body;

    public AstFunction(AstFunctionHeader header, AstFunctionBody body) {
        this.header = header;
        this.body = body;
    }

    public AstFunctionHeader getHeader() {
        return header;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AstFunction function = (AstFunction) o;
        return header.equals(function.header);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header);
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException, AlreadyDeclaredException, BadArithmeticExpressionException {
        formalParameters.setVariables(new ArrayList<>());
        declaredVariables.setVariables(new ArrayList<>());
        StringBuilder generatedCode = new StringBuilder();

        generatedCode.append(header.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, this));
        generatedCode.append(body.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, this));
        generatedCode.append("}\n");

        return generatedCode.toString();
    }
}
