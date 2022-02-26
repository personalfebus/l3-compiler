package ru.bmstu.iu9.personalfebus.compiler.ast;

import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstIdentExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstType;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.generator.Generatable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AstFunctionHeader implements Generatable {
    private final String name;
    private Set<AstVariable> variables;
    private AstType returnType;

    public AstFunctionHeader(String name) {
        this.name = name;
        this.returnType = null;
    }

    public void setReturnType(AstType returnType) {
        this.returnType = returnType;
    }

    public void setVariables(Set<AstVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(AstVariable variable) {
        variables.add(variable);
    }

    public String getName() {
        return name;
    }

    public Set<AstVariable> getVariables() {
        return variables;
    }

    public AstType getReturnType() {
        return returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AstFunctionHeader header = (AstFunctionHeader) o;
        return name.equals(header.name) && Objects.equals(variables, header.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, variables);
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper) throws MissingException {
        List<AstVariable> list = new ArrayList<>(variables);
        formalParameters.setVariables(list);
        StringBuilder generatedCode = new StringBuilder();
        generatedCode.append(".method public hidebysig static ")
                .append(returnType.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper))
                .append(" ")
                .append(name)
                .append("(");

        for (AstVariable variable : variables) {
            generatedCode.append(variable.generateDeclarationIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper))
                    .append(", ");
        }
        generatedCode.replace(generatedCode.length() - 2, generatedCode.length(), ")");
        generatedCode.append(" cil managed");
        generatedCode.append(" {\n");
        if (name.equalsIgnoreCase("main")) generatedCode.append(".entrypoint\n");
        return generatedCode.toString();
    }
}
