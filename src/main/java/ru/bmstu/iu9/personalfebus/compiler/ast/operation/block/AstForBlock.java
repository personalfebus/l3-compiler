package ru.bmstu.iu9.personalfebus.compiler.ast.operation.block;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstFunction;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstOperation;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstVariableAssigment;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.AstVariableInitialization;
import ru.bmstu.iu9.personalfebus.compiler.ast.operation.model.InitializationOrAssigment;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstArithExpr;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstArithExprIdentConstant;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.AstArithOperator;
import ru.bmstu.iu9.personalfebus.compiler.ast.value.RValue;
import ru.bmstu.iu9.personalfebus.compiler.ast.variable.AstVariable;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.generator.exception.MissingException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.AlreadyDeclaredException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.BadArithmeticExpressionException;
import ru.bmstu.iu9.personalfebus.compiler.parser.exception.TypeIncompatibilityException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AstForBlock implements AstOperation {
    private final InitializationOrAssigment ioa;
    private final RValue to;
    private final RValue step;
    private final List<AstOperation> operations;

    public final static String TYPE = "FOR_BLOCK";

    public AstForBlock(InitializationOrAssigment ioa, RValue to, RValue step, List<AstOperation> operations) {
        this.ioa = ioa;
        this.to = to;
        this.step = step;
        this.operations = operations;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String generateIL(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, StringBuilder locals, AstFunction currentFunction) throws MissingException, TypeIncompatibilityException, AlreadyDeclaredException, BadArithmeticExpressionException {
        int labelNum = labelGenerationHelper.getNum();
        StringBuilder generatedCode = new StringBuilder();

        if (ioa.isInit) {
            AstVariableInitialization init = new AstVariableInitialization(ioa.variables);
            generatedCode.append(init.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        } else {
            AstVariableAssigment assigment = new AstVariableAssigment(ioa.variables);
            generatedCode.append(assigment.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        }
        generatedCode.append("br for_" + labelNum + "_conditions\n");
        generatedCode.append("for_" + labelNum + "_operations:\n");
        for (AstOperation operation : operations) {
            generatedCode.append(operation.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        }

        //step
        generatedCode.append(generateStep(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        generatedCode.append("for_" + labelNum + "_conditions:\n");

        //condtion
        String loadCoomand = "";
        if (ioa.variables.size() == 0) {
            throw new MissingException("for variable declaration");
        }

        for (AstVariable var : ioa.variables) {
            if (formalParameters.hasVariable(var)) {
                loadCoomand = "ldarg." + formalParameters.getVariableIndex(var);
            } else if (declaredVariables.hasVariable(var)) {
                loadCoomand = "ldloc." + declaredVariables.getVariableIndex(var);
            } else throw new MissingException("for variable declaration");
            break;
        }
        generatedCode.append(loadCoomand)
                .append("\n");
        generatedCode.append(to.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, currentFunction));
        generatedCode.append("ble for_" + labelNum + "_operations\n");
        return generatedCode.toString();
    }

    private String generateStep(Set<AstFunction> declaredFunctions, VariableNameTranslator formalParameters, VariableNameTranslator declaredVariables, LabelGenerationHelper labelGenerationHelper, StringBuilder locals, AstFunction currentFunction) throws MissingException, BadArithmeticExpressionException, TypeIncompatibilityException {
        StringBuilder generatedCode = new StringBuilder();
        AstVariable first = null;

        if (ioa.variables.size() == 0) {
            throw new MissingException("for variable declaration");
        }

        for (AstVariable var : ioa.variables) {
            first = new AstVariable(var.getExpr());
            break;
        }

        AstArithExpr expr = (AstArithExpr) step;
        expr.addPart(new AstArithOperator('+', 2));
        expr.addPart(new AstArithExprIdentConstant(first.getExpr().getName(), first.getExpr().getTail()));
        expr.emptyStack();

        first.setrValue(expr);
        Set<AstVariable> vr = new HashSet<>();
        vr.add(first);
        AstVariableAssigment assigment = new AstVariableAssigment(vr);
        generatedCode.append(assigment.generateIL(declaredFunctions, formalParameters, declaredVariables, labelGenerationHelper, locals, currentFunction));
        return generatedCode.toString();
    }
}
