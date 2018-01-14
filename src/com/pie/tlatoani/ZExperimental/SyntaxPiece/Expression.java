package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import java.util.Set;

/**
 * Created by Tlatoani on 8/21/17.
 */
public class Expression extends SyntaxPiece {
    public final String variable;
    public final ExpressionConstraints constraints;

    public Expression(String variable, String exprInfo) {
        this.variable = variable;
        this.constraints = ExpressionConstraints.fromSyntax(exprInfo);
    }

    @Override
    public boolean containsVariables() {
        return true;
    }

    @Override
    public VariableUsage getVariableUsage(String variable) {
        return this.variable.equals(variable)
                ? (constraints.nullable
                        ? VariableUsage.SPECIFIC
                        : VariableUsage.CONSISTENT)
                : VariableUsage.NONE;
    }

    @Override
    public int markLength() {
        return 0;
    }

    @Override
    public int expressionAmount() {
        return 1;
    }

    @Override
    public void addVariables(VariableCollective collective) {
        collective.addExpression(variable, constraints);
    }

    @Override
    public String readableSyntax() {
        return "%" + constraints.getTypeOptions() + "%";
    }

    @Override
    public String actualSyntax(int prevMarkLength) {
        return "%" + constraints.getSyntax() + "%";
    }

    @Override
    public String originalSyntax() {
        return "%" + variable + "=" + constraints.getSyntax() + "%";
    }

    @Override
    public void setInformation(MarkSpecificInformation information, int mark, int prevExprAmount) {
        information.exprIndexes.put(variable, prevExprAmount);
    }

    @Override
    public String toString(int mark) {
        return "%" + variable + "%";
    }

    @Override
    public String toString() {
        return "Expression(\"" + variable + "\", \"" + constraints + "\")";
    }
}
